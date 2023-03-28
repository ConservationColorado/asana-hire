package org.conservationco.asanahire.service

import com.asana.models.Portfolio
import com.asana.models.Project
import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.exception.EmptyPortfolioException
import org.conservationco.asanahire.exception.MismatchedHiringProjectsException
import org.conservationco.asanahire.model.asana.JobSource
import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.exception.JobNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2
import java.util.logging.Logger

@Service
class JobService(
    private val jobRepository: JobRepository,
    @Autowired private val jobSource: JobSource,
) {

    private val logger = Logger.getLogger(JobService::class.qualifiedName)

    fun getLocalOrFetchJob(jobId: Long): Mono<Job> =
        jobRepository
            .findById(jobId)
            .switchIfEmpty(
                Mono.error(JobNotFoundException("Job with ID $jobId not found!"))
            )

    fun getLocalOrFetchJobs() =
        jobRepository
            .count()
            .doOnNext {
                if (it == 0L) {
                    logger.info("No jobs in repository... fetching them.")
                    fetchAndBuildJobs()
                }
            }
            .flatMapMany { jobRepository.findAll() }
            .collectList()

    private fun fetchAndBuildJobs() =
        collectJobs()
            .flatMapMany { Flux.fromIterable(it) }
            .flatMap { jobRepository.save(it) }
            .doOnComplete { logger.info("Saved new jobs to repository.") }
            .doOnError { logger.severe("Error occurred while saving jobs to repository.") }
            .subscribe()

    private fun collectJobs() =
        Mono
            .zip(
                extractProjects(jobSource.applicationPortfolio, "application"),
                extractProjects(jobSource.interviewPortfolio, "interview")
            )
            .flatMap { tuple -> errorFilter(tuple) }
            .flatMap { tuple -> collectTupleToCollection(tuple) }

    private fun collectTupleToCollection(
        tuple: Tuple2<Map<String, String>, Map<String, String>>
    ): Mono<Collection<Job>> {
        val applicationProjects = tuple.t1
        val interviewProjects = tuple.t2

        val jobs = (applicationProjects.keys.asSequence() + interviewProjects.keys)
            .associateWith { it ->
                Job().apply {
                    title = it
                    applicationProjectId = applicationProjects[it].orEmpty()
                    interviewProjectId = interviewProjects[it].orEmpty()
                }
            }.values

        return Mono.just(jobs)
    }

    private fun errorFilter(
        tuple: Tuple2<Map<String, String>, Map<String, String>>
    ): Mono<Tuple2<Map<String, String>, Map<String, String>>> {
        val applicationProjectCount = tuple.t1.size
        val interviewProjectCount = tuple.t2.size
        val applicationProjectsExist = applicationProjectCount != 0
        val interviewProjectsExist = interviewProjectCount != 0

        return when {
            !interviewProjectsExist -> Mono.error(EmptyPortfolioException("No projects in the application portfolio!"))
            !applicationProjectsExist -> Mono.error(EmptyPortfolioException("No projects in the interview portfolio!"))
            interviewProjectCount != applicationProjectCount ->
                Mono.error(
                    MismatchedHiringProjectsException(
                        "Job project counts must match across application and interview portfolios:" +
                                "$applicationProjectCount application projects != $interviewProjectCount interview projects!"
                    )
                )

            else -> Mono.just(tuple)
        }
    }

    private fun extractProjects(
        portfolio: Portfolio,
        keyword: String
    ): Mono<Map<String, String>> = asanaContext {
        Mono.fromCallable {
            portfolio
                .getItems()
                .filter { keywordFilter(it, keyword) }
        }.mapProjectNamesToIds()
    }

    private val keywordFilter: (Project, String) -> Boolean = { project, keyword ->
        project.name.contains(keyword, ignoreCase = true)
    }

    private fun Mono<List<Project>>.mapProjectNamesToIds() =
        map { list -> list.associateBy({ extractJobTitle(it.name) }, { it.gid }) }

    private fun extractJobTitle(projectName: String): String {
        val separator = ": "
        val titleStart = projectName.indexOf(separator) + separator.length
        return projectName.substring(titleStart).trim()
    }

}

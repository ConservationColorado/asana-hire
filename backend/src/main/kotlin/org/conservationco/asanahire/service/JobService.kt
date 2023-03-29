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
                Mono.error(JobNotFoundException(jobId))
            )

    fun getLocalOrFetchJobs() =
        jobRepository
            .findAll()
            .switchIfEmpty(fetchAndBuildJobs())
            .collectList()

    private fun fetchAndBuildJobs() =
        collectJobs()
            .flatMapMany { Flux.fromIterable(it) }
            .flatMap { jobRepository.save(it) }
            .doOnComplete { logger.info("Saved new jobs to repository") }

    private fun collectJobs() =
        Mono
            .zip(
                jobSource.applicationPortfolio.extractProjects("application"),
                jobSource.interviewPortfolio.extractProjects("interview")
            )
            .flatMap { errorFilter(it) }
            .flatMap { combinePortfolios(it) }

}

// Helper functions

private fun combinePortfolios(
    tuple: Tuple2<Map<String, String>, Map<String, String>>
): Mono<Collection<Job>> {
    val applicationProjects = tuple.t1
    val interviewProjects = tuple.t2

    val jobs = (applicationProjects.keys.asSequence() + interviewProjects.keys)
        .associateWith {
            Job().apply {
                title = it
                applicationProjectId = applicationProjects[it].orEmpty()
                interviewProjectId = interviewProjects[it].orEmpty()
            }
        }.values

    return Mono.just(jobs)
}

private fun Portfolio.extractProjects(
    keyword: String
): Mono<Map<String, String>> = asanaContext {
    Mono.fromCallable {
        getItems()
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

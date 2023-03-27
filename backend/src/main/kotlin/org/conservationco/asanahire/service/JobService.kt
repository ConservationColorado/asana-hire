package org.conservationco.asanahire.service

import com.asana.models.Portfolio
import com.asana.models.Project
import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.exception.EmptyPortfolioException
import org.conservationco.asanahire.exception.MismatchedHiringProjectsException
import org.conservationco.asanahire.model.asana.JobSource
import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.repository.JobRepository
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

    val keywordFilter: (Project, String) -> Boolean = { project, keyword ->
        project.name.contains(keyword, ignoreCase = true)
    }

    fun getJob(jobId: Long): Mono<Job> {
        return verifyProjects()
            .flatMap { jobRepository.findById(jobId) }
    }

    fun getJobs(): Flux<Job> {
        return verifyProjects()
            .flatMapMany { jobRepository.findAll() }
    }

    private fun forceReloadProjects(): Mono<Void> {
        val jobs: Flux<Job> = collectJobs().flatMapMany { Flux.fromIterable(it) }
        return jobRepository
            .deleteAll()
            .doOnSuccess { logger.info("Deleted jobs from repository. Saving new jobs now.") }
            .flatMapMany { jobRepository.saveAll(jobs) }
            .doOnComplete { logger.info("Saved new jobs to repository.") }
            .doOnError { logger.severe("Error occurred while saving jobs to repository.") }
            .then()
    }

    private fun collectJobs(): Mono<Collection<Job>> {
        val applicationProjectsMono = Mono.fromCallable {
            extractProjects(jobSource.applicationPortfolio, "application")
        }
        val interviewProjectsMono = Mono.fromCallable {
            extractProjects(jobSource.interviewPortfolio, "interview")
        }
        val applicationProjects = mapProjectNamesToIds(applicationProjectsMono)
        val interviewProjects = mapProjectNamesToIds(interviewProjectsMono)

        return validateProjects(applicationProjects, interviewProjects)
            .flatMap { unifyProjectsToJobList(applicationProjects, interviewProjects) }
    }

    private fun unifyProjectsToJobList(
        applicationProjectsMono: Mono<Map<String, String>>,
        interviewProjectsMono: Mono<Map<String, String>>
    ): Mono<Collection<Job>> {
        return Mono
            .zip(applicationProjectsMono, interviewProjectsMono)
            .map { mapJobProjectsToSingleJobCollection(it) }
    }

    private fun mapJobProjectsToSingleJobCollection(
        tuple: Tuple2<Map<String, String>, Map<String, String>>
    ): Collection<Job> {
        val applicationProjects = tuple.t1
        val interviewProjects = tuple.t2
        return (applicationProjects.keys.asSequence() + interviewProjects.keys)
            .associateWith {
                val job = Job()
                job.title = it
                job.applicationProjectId = applicationProjects[it].orEmpty()
                job.interviewProjectId = interviewProjects[it].orEmpty()
                job
            }.values
    }

    private fun mapProjectNamesToIds(projectsMono: Mono<List<Project>>) =
        projectsMono
            .map { list -> list.associateBy({ extractJobTitle(it.name) }, { it.gid }) }

    private fun validateProjects(
        applicationProjectsMono: Mono<Map<String, String>>,
        interviewProjectsMono: Mono<Map<String, String>>
    ): Mono<Void> {
        return Mono
            .zip(applicationProjectsMono, interviewProjectsMono)
            .flatMap { checkAndMapAnyErrors(it) }
    }

    private fun checkAndMapAnyErrors(it: Tuple2<Map<String, String>, Map<String, String>>): Mono<Void> {
        val applicationProjectCount = it.t1.size
        val interviewProjectCount = it.t2.size
        val applicationProjectsExist = applicationProjectCount != 0
        val interviewProjectsExist = interviewProjectCount != 0

        return if (!interviewProjectsExist) {
            Mono.error(EmptyPortfolioException("No projects in the application portfolio!"))
        } else if (!applicationProjectsExist) {
            Mono.error(EmptyPortfolioException("No projects in the interview portfolio!"))
        } else if (interviewProjectCount != applicationProjectCount) {
            Mono.error(
                MismatchedHiringProjectsException(
                    "Job project counts must match across application and interview portfolios:" +
                            "$applicationProjectCount application projects != $interviewProjectCount interview projects !"
                )
            )
        } else {
            Mono.empty()
        }
    }

    private fun extractProjects(
        portfolio: Portfolio,
        keyword: String
    ): List<Project> = asanaContext {
        portfolio
            .getItems()
            .filter { keywordFilter(it, keyword) }
    }

    private fun extractJobTitle(projectName: String): String {
        val separator = ": "
        val titleStart = projectName.indexOf(separator) + separator.length
        return projectName.substring(titleStart).trim()
    }

    private fun verifyProjects() = jobRepository
        .count()
        .map { if (it == 0L) forceReloadProjects() }

}

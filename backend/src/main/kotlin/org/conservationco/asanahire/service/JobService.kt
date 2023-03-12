package org.conservationco.asanahire.service

import com.asana.models.Portfolio
import com.asana.models.Project
import kotlinx.coroutines.*
import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.model.asana.JobSource
import org.conservationco.asanahire.exception.EmptyPortfolioException
import org.conservationco.asanahire.exception.MismatchedHiringProjectsException
import org.conservationco.asanahire.repository.JobRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JobService(
    private val jobRepository: JobRepository,
    @Autowired private val jobSource: JobSource,
) {

    private val jobScope = CoroutineScope(SupervisorJob())

    val keywordFilter: (Project, String) -> Boolean = { project, keyword ->
        project.name.contains(keyword, ignoreCase = true)
    }

    suspend fun getJob(jobId: Long): Job = withContext(Dispatchers.IO) {
        verifyProjects()
        return@withContext jobRepository
            .findById(jobId)
            .orElseThrow()
    }

    suspend fun getJobs(): Iterable<Job> = withContext(Dispatchers.IO) {
        verifyProjects()
        return@withContext jobRepository.findAll()
    }

    suspend fun forceReloadProjects(): Unit = asanaContext {
        val jobs = jobScope.async {
            val applicationProjectsDeferred = async {
                extractProjects(jobSource.applicationPortfolio, "application")
            }
            val interviewProjectsDeferred = async {
                extractProjects(jobSource.interviewPortfolio, "interview")
            }

            val applicationProjects = awaitAndTransformProjects(applicationProjectsDeferred)
            val interviewProjects = awaitAndTransformProjects(interviewProjectsDeferred)

            validateProjects(applicationProjects, interviewProjects)

            return@async unifyProjectsToJobList(applicationProjects, interviewProjects)
        }
        jobRepository.deleteAll()
        jobRepository.saveAll(jobs.await())
    }

    private fun unifyProjectsToJobList(
        applicationProjects: Map<String, String>,
        interviewProjects: Map<String, String>
    ) = (applicationProjects.keys.asSequence() + interviewProjects.keys)
        .associateWith {
            val job = Job()
            job.title = it
            job.applicationProjectId = applicationProjects[it].orEmpty()
            job.interviewProjectId = interviewProjects[it].orEmpty()
            job
        }.values

    private suspend fun awaitAndTransformProjects(projectsDeferred: Deferred<List<Project>>) =
        projectsDeferred
            .await()
            .associateBy( { extractJobTitle(it.name) }, { it.gid } )

    private fun validateProjects(
        applicationProjects: Map<String, String>,
        interviewProjects: Map<String, String>
    ) {
        val interviewProjectCount = interviewProjects.size
        val applicationProjectCount = applicationProjects.size

        if (applicationProjectCount == 0) throw EmptyPortfolioException("No projects in the application portfolio!")
        if (interviewProjectCount == 0) throw EmptyPortfolioException("No projects in the interview portfolio!")

        if (interviewProjectCount != applicationProjectCount) throw MismatchedHiringProjectsException(
            "Job project counts must match across application and interview portfolios: " +
                    "$applicationProjectCount application projects != $interviewProjectCount interview projects !"
        )
    }

    private suspend fun extractProjects(
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

    private suspend fun verifyProjects() {
        withContext(Dispatchers.IO) {
            val localProjects = jobRepository.count()
            if (localProjects == 0L) forceReloadProjects()
        }
    }

}

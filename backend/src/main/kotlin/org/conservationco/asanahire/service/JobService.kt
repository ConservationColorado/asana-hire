package org.conservationco.asanahire.service

import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.domain.JobSource
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.asanahire.repository.getJob
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class JobService(
    private val jobRepository: JobRepository,
    @Autowired private val jobSource: JobSource,
) {

    fun getJob(jobId: String): Job {
        verifyJobs()
        return jobRepository
            .findById(jobId)
            .orElseThrow()
    }

    fun getJobs(): Iterable<Job> {
        verifyJobs()
        return jobRepository.findAll()
    }

    private fun verifyJobs() {
        fun remoteJobCount() = asanaContext { jobSource.project.getTaskCount() }.toLong()
        val localJobs = jobRepository.count()
        if (localJobs == 0L || localJobs != remoteJobCount()) loadJobsFromRemote()
    }

    private fun loadJobsFromRemote() = asanaContext {
        val jobs = jobSource.project.convertTasksToListOf(Job::class, false) { task, job ->
            job.id = task.gid
            job.title = task.name
        }
        jobRepository.deleteAll()
        jobRepository.saveAll(jobs)
    }

}

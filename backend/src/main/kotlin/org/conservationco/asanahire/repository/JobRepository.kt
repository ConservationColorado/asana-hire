package org.conservationco.asanahire.repository

import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.exception.NoSuchJobException
import org.springframework.data.repository.CrudRepository

interface JobRepository : CrudRepository<Job, Long>

fun <E> JobRepository.getJob(jobId: Long, runIfPresent: (Job) -> E) =
    findById(jobId)
        .map { runIfPresent(it) }
        .orElseThrow { throw NoSuchJobException() }

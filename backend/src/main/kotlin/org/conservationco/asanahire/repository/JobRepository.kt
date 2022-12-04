package org.conservationco.asanahire.repository

import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.exception.NoSuchJobException
import org.springframework.data.repository.CrudRepository

interface JobRepository : CrudRepository<Job, String>

fun JobRepository.getJob(jobId: String, runIfPresent: (Job) -> Unit) =
    findById(jobId)
        .ifPresentOrElse(runIfPresent) { throw NoSuchJobException() }

package org.conservationco.asanahire.repository

import org.conservationco.asanahire.model.job.Job
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface JobRepository : ReactiveCrudRepository<Job, Long>

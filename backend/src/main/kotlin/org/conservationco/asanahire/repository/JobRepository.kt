package org.conservationco.asanahire.repository

import org.conservationco.asanahire.model.job.Job
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface JobRepository : ReactiveCrudRepository<Job, Long> {
    fun findFirstByApplicationProjectId(id: String?): Mono<Job>
    fun findFirstByInterviewProjectId(id: String?): Mono<Job>
}

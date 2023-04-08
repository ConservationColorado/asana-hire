package org.conservationco.asanahire.repository

import org.conservationco.asanahire.model.sync.SyncEvent
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface SyncEventRepository : ReactiveCrudRepository<SyncEvent, Long> {
    fun findByJobId(jobId:  Long): Mono<SyncEvent>
}

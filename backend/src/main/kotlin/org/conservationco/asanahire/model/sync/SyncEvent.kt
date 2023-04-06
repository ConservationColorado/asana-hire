package org.conservationco.asanahire.model.sync

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "SYNC_EVENT")
data class SyncEvent(
    @Id
    val id: Long,
    val jobId: Long,
    val createdOn: LocalDateTime,
    val status: RequestState = RequestState.NOT_STARTED,
) {
    fun isStarted() = status != RequestState.NOT_STARTED
    fun isComplete() = status == RequestState.COMPLETE
    fun isInProgress() = status == RequestState.IN_PROGRESS
}

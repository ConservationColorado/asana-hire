package org.conservationco.asanahire.model.sync

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("SYNC_EVENT")
data class SyncEvent(
    @Id
    val id: Long = 0L,
    @Column("JOB_ID")
    val jobId: Long = 0L,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    val status: RequestState = RequestState.NOT_STARTED,
)

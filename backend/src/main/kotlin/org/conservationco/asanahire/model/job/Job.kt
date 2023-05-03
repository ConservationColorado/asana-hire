package org.conservationco.asanahire.model.job

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "Job")
data class Job(
    @Id
    val id: Long = 0L,
    val title: String = "",
    val applicationProjectId: String = "",
    val interviewProjectId: String = "",
    val isAutoSyncing: Boolean = false,
)

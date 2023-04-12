package org.conservationco.asanahire.model.job

import org.conservationco.asana.serialization.AsanaSerializable
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "Job")
data class Job(
    @Id
    var id: Long = 0L,
    var title: String = "",
    var applicationProjectId: String = "",
    var interviewProjectId: String = "",
) : AsanaSerializable<Job>

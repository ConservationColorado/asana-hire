package org.conservationco.asanahire.model.job

import org.conservationco.asana.serialization.AsanaSerializable
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "Job")
open class Job : AsanaSerializable<Job> {
    @Id
    open var id: Long = 0L
    open var title: String = ""
    open var applicationProjectId: String = ""
    open var interviewProjectId: String = ""
}

package org.conservationco.asanahire.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.conservationco.asana.serialization.AsanaSerializable

@Entity
open class Job : AsanaSerializable<Job> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    open var id: Long = 0L
    open var title: String = ""
    open var applicationProjectId: String = ""
    open var interviewProjectId: String = ""
}

package org.conservationco.asanahire.domain

import jakarta.persistence.*
import org.conservationco.asana.serialization.AsanaSerializable
import org.conservationco.asana.serialization.customfield.AsanaCustomField

@Entity
open class Job : AsanaSerializable<Job> {
    @Id open var id: String = ""
    open var title: String = ""
    @AsanaCustomField("Origin project gid")     open var originalSourceId: String = ""
    @AsanaCustomField("Manager project gid")    open var managerSourceId: String = ""
    @AsanaCustomField("Status")                 open var status: String = ""
    @AsanaCustomField("Team")                   open var team: String = ""
    @AsanaCustomField("Hiring manager email")   open var hiringManagerEmail: String = ""
    @AsanaCustomField("Made hire?")             open var madeHire: String = ""
}

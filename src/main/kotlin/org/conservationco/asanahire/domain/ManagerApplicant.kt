package org.conservationco.asanahire.domain

import com.asana.models.Attachment
import org.conservationco.asana.serialization.AsanaSerializable
import org.conservationco.asana.serialization.customfield.AsanaCustomField

/**
 * Data class for POSTing [OriginalApplicant] objects to a manager context.
 */
internal data class ManagerApplicant(
    override var id: String = "",
    override var name: String = "",
    override var documents: Collection<Attachment> = emptyList(),
    @AsanaCustomField("Preferred Name")     override var preferredName: String = "",
    @AsanaCustomField("Pronouns")           override var pronouns: String = "",
    @AsanaCustomField("Pronouns (other)")   override var pronounsOther: String = "",
    @AsanaCustomField("Email")              override var email: String = "",
    @AsanaCustomField("Phone Number")       override var phoneNumber: String = "",
    @AsanaCustomField("References")         override var references: String = "",
    @AsanaCustomField("Rating")             var hiringManagerRating: String = "",
    @AsanaCustomField("Interview stage")    var interviewStage: String = "",
    @AsanaCustomField("Interview substage") var interviewSubstage: String = "",
) : Applicant, AsanaSerializable<ManagerApplicant>

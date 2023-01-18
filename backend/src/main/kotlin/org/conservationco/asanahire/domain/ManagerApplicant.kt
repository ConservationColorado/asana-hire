package org.conservationco.asanahire.domain

import com.asana.models.Attachment
import org.conservationco.asana.asanaContext
import org.conservationco.asana.serialization.AsanaSerializable
import org.conservationco.asana.serialization.customfield.AsanaCustomField
import org.conservationco.asanahire.util.applicantDeserializingFn

/**
 * Data class for POSTing [OriginalApplicant] objects to a manager context.
 */
data class ManagerApplicant(
    override var id: String = "",
    override var name: String = "",
    override var documents: Collection<Attachment> = emptyList(),
    @AsanaCustomField("Preferred name")     override var preferredName: String = "",
    @AsanaCustomField("Pronouns")           override var pronouns: String = "",
    @AsanaCustomField("Pronouns (other)")   override var pronounsOther: String = "",
    @AsanaCustomField("Email")              override var email: String = "",
    @AsanaCustomField("Phone number")       override var phoneNumber: String = "",
    @AsanaCustomField("References")         override var references: String = "",
    @AsanaCustomField("Rating")             var hiringManagerRating: String = "",
    @AsanaCustomField("Interview stage")    var interviewStage: String = "",
    @AsanaCustomField("Interview substage") var interviewSubstage: String = "",
) : Applicant, AsanaSerializable<ManagerApplicant>

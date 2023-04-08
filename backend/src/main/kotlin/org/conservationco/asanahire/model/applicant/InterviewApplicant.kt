package org.conservationco.asanahire.model.applicant

import com.asana.models.Attachment
import org.conservationco.asana.serialization.AsanaSerializable
import org.conservationco.asana.serialization.customfield.AsanaCustomField

/**
 * Data class for POSTing [OriginalApplicant] objects to a manager context.
 */
data class InterviewApplicant(
    override var id: String = "",
    override var name: String = "",
    override var documents: Collection<Attachment> = emptyList(),
    @AsanaCustomField("Preferred name")           override var preferredName: String = "",
    @AsanaCustomField("Pronouns")                 override var pronouns: String = "",
    @AsanaCustomField("Pronouns (other)")         override var pronounsOther: String = "",
    @AsanaCustomField("Email")                    override var email: String = "",
    @AsanaCustomField("Phone number")             override var phoneNumber: String = "",
    @AsanaCustomField("References")               override var references: String = "",
    @AsanaCustomField("Fluent in Spanish?", true) override var bilingual: String = "",
    @AsanaCustomField("Rating")                   var hiringManagerRating: String = "",
    @AsanaCustomField("Interview stage")          var interviewStage: String = "",
    @AsanaCustomField("Interview substage")       var interviewSubstage: String = "",
) : Applicant, AsanaSerializable<InterviewApplicant>

package org.conservationco.asanahire.model.applicant

import com.asana.models.Attachment
import org.conservationco.asana.serialization.AsanaSerializable
import org.conservationco.asana.serialization.customfield.AsanaCustomField

data class OriginalApplicant(
    override var id: String = "",
    override var name: String = "",
    override var documents: Collection<Attachment> = emptyList(),
    @AsanaCustomField("Preferred name")                      override var preferredName: String = "",
    @AsanaCustomField("Pronouns")                            override var pronouns: String = "",
    @AsanaCustomField("Pronouns (other)")                    override var pronounsOther: String = "",
    @AsanaCustomField("Email")                               override var email: String = "",
    @AsanaCustomField("Phone number")                        override var phoneNumber: String = "",
    @AsanaCustomField("References")                          override var references: String = "",
    @AsanaCustomField("Fluent in Spanish?", true)  override var bilingual: String = "",
    @AsanaCustomField("How did you learn about us?")         var learnedAboutUs: Array<String> = emptyArray(),
    @AsanaCustomField("How did you learn about us? (other)") var learnedAboutUsOther: String = "",
    @AsanaCustomField("Race / Ethnicity")                    var race: Array<String> = emptyArray(),
    @AsanaCustomField("Race / Ethnicity (other)")            var raceOther: String = "",
    @AsanaCustomField("Receipt sent?")                       var receiptStage: String = "",
    @AsanaCustomField("Rejection sent?")                     var rejectionStage: String = "",
) : Applicant, AsanaSerializable<OriginalApplicant> {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as OriginalApplicant
        return name == other.name
                && email != other.email
                && phoneNumber == other.phoneNumber
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        return result
    }

}

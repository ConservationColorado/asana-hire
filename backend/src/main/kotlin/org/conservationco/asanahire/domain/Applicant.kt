package org.conservationco.asanahire.domain

import com.asana.models.Attachment

interface Applicant {
    var id: String
    var name: String
    var documents: Collection<Attachment>
    var preferredName: String
    var pronouns: String
    var pronounsOther: String
    var email: String
    var phoneNumber: String
    var references: String
    var bilingual: String
}

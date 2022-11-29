package org.conservationco.asanahire.util

import com.asana.models.Task
import org.conservationco.asanahire.domain.Applicant
import org.conservationco.asanahire.domain.ManagerApplicant
import org.conservationco.asanahire.domain.OriginalApplicant

internal fun <A : Applicant> applicantSerializingFn(): (A, Task) -> Unit =
    { source, destination -> destination.serialize(source) }

internal fun <A : Applicant> applicantDeserializingFn(): (Task, A) -> Unit =
    { source, destination -> destination.deserialize(source) }

internal fun <A : Applicant> Task.serialize(source: A) {
    name = source.name
    gid = source.id
    attachments = source.documents
}

internal fun <A : Applicant> A.deserialize(source: Task) {
    name = source.name
    id = source.gid
    documents = source.attachments
}

internal fun OriginalApplicant.toManagerApplicant(): ManagerApplicant {
    val copy = copy()
    return ManagerApplicant(
        name = copy.name,
        documents = copy.documents,
        preferredName = copy.preferredName,
        pronouns = copy.pronouns,
        pronounsOther = copy.pronounsOther,
        email = copy.email,
        phoneNumber = copy.phoneNumber,
        references = copy.references
    )
}

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
        id = copy.id,
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

internal fun OriginalApplicant.needsSyncing(): Boolean = receiptStage.isEmpty()

internal fun ManagerApplicant.isInterviewing(): Boolean = interviewStage.isNotEmpty() || interviewSubstage.isNotEmpty()

/**
 * Use this function to ensure that no communication is sent *after* the receiver applicant is rejected. If a candidate
 * is rejected, they should not receive any further communication from us.
 */
internal fun OriginalApplicant.hasBeenCommunicatedTo(): Boolean {
    return receiptStage.isNotEmpty() || rejectionStage.isNotEmpty()
}

internal fun OriginalApplicant.communicate() {
    if (!hasBeenCommunicatedTo()) receiptStage = "✅"
}

internal fun OriginalApplicant.hasBeenRejected(): Boolean = rejectionStage.isNotEmpty()

/**
 * If applicant is in process of interview, do not reject them either! If an applicant was already rejected do not
 * message them again.
 */
internal fun ManagerApplicant.needsRejection(): Boolean = hiringManagerRating == "No" && !isInterviewing()

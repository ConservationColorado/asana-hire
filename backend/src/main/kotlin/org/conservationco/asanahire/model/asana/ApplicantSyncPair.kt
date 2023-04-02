package org.conservationco.asanahire.model.asana

import org.conservationco.asanahire.model.applicant.InterviewApplicant
import org.conservationco.asanahire.model.applicant.OriginalApplicant

data class ApplicantSyncPair(
    val originalApplicant: OriginalApplicant,
    val interviewApplicant: InterviewApplicant,
)

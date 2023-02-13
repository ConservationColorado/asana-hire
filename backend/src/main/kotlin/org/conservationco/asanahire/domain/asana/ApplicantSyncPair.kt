package org.conservationco.asanahire.domain.asana

import org.conservationco.asanahire.domain.InterviewApplicant
import org.conservationco.asanahire.domain.OriginalApplicant

data class ApplicantSyncPair(
    val originalApplicant: OriginalApplicant,
    val interviewApplicant: InterviewApplicant,
)
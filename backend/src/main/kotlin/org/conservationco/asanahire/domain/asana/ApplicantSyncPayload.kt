package org.conservationco.asanahire.domain.asana

import org.conservationco.asanahire.domain.ManagerApplicant
import org.conservationco.asanahire.domain.OriginalApplicant

data class ApplicantSyncPayload(
    val originalApplicant: OriginalApplicant,
    val interviewApplicant: ManagerApplicant,
)

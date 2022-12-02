package org.conservationco.asanahire.domain

import com.asana.models.Task

data class ApplicantPayload(
    val originalApplicant: OriginalApplicant,
    val managerApplicant: ManagerApplicant,
    val originalTask: Task,
    val managerTask: Task,
)

package org.conservationco.asanahire.domain.asana

import com.asana.models.Task
import org.conservationco.asanahire.domain.ManagerApplicant
import org.conservationco.asanahire.domain.OriginalApplicant

data class ApplicantPayload(
    val originalApplicant: OriginalApplicant,
    val managerApplicant: ManagerApplicant,
    val originalTask: Task,
    val managerTask: Task,
)

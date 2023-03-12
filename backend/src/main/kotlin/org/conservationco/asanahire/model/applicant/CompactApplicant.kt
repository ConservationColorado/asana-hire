package org.conservationco.asanahire.model.applicant

import com.asana.models.Task

data class ApplicantEvent(
    val id: String,
    val name: String,
)

internal fun Task.toApplicantEvent(): ApplicantEvent = ApplicantEvent(
    id = gid,
    name = name
)

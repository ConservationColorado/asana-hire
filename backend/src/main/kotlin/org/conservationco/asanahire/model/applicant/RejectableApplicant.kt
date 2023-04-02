package org.conservationco.asanahire.model.applicant

data class RejectableApplicant(
    val id: String,
    val jobId: Long,
    val name: String,
    val preferredName: String,
    val email: String,
)

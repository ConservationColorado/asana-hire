package org.conservationco.asanahire.domain

data class RejectableApplicant(
    val id: String,
    val jobId: Long,
    val name: String,
    val preferredName: String,
    val email: String,
)

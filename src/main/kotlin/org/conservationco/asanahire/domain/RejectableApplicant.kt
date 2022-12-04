package org.conservationco.asanahire.domain

data class RejectableApplicant(
    val originalGid: String,
    val managerGid: String,
    val name: String,
    val preferredName: String,
    val email: String,
)

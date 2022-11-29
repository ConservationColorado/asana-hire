package org.conservationco.asanahire.domain

import java.time.LocalDateTime

data class Job(
    val id: String,
    val originalSourceId: String = "",
    var managerSourceId: String = "",
    val title: String = "",
    val startOn: LocalDateTime = LocalDateTime.MIN,
    val endOn: LocalDateTime = LocalDateTime.MIN,
    val status: String = "",
    val team: String = "",
    val hiringManagerEmail: String = "",
    val madeHire: String = "",
)

package org.conservationco.asanahire.model.user

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "`User`")
data class User(
    @Id
    val id: Long = 0L,
    val name: String = "",
    val email: String = "",
    val picture: String = "",
    val provider: AuthProvider = AuthProvider.LOCAL,
)

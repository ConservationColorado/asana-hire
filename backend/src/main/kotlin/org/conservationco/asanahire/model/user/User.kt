package org.conservationco.asanahire.model.user

import org.springframework.data.annotation.Id;
import org.conservationco.asanahire.security.AuthProvider
import org.springframework.data.relational.core.mapping.Table

@Table(name = "`User`")
open class User(
    @Id
    open var id: String = "",
    open var name: String = "",
    open var email: String = "",
    open var picture: String = "",
    open var provider: AuthProvider = AuthProvider.LOCAL,
)

package org.conservationco.asanahire.model.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open var id: String = "",
    open var name: String = "",
    open var email: String = "",
    open var picture: String = "",
)

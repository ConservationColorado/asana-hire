package org.conservationco.asanahire.model.user

import org.springframework.data.annotation.Id
import org.conservationco.asanahire.security.AuthProvider
import org.springframework.data.relational.core.mapping.Table

@Table(name = "`User`")
open class User(
    @Id
    open var id: Long = 0L,
    open var name: String = "",
    open var email: String = "",
    open var picture: String = "",
    open var provider: AuthProvider = AuthProvider.LOCAL,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (name != other.name) return false
        if (email != other.email) return false
        if (picture != other.picture) return false
        if (provider != other.provider) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + picture.hashCode()
        result = 31 * result + provider.hashCode()
        return result
    }

    override fun toString(): String {
        return "User(id=$id, name='$name', email='$email', picture='$picture', provider=$provider)"
    }

}

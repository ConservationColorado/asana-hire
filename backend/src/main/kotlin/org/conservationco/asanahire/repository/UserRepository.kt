package org.conservationco.asanahire.repository

import org.conservationco.asanahire.model.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?
}

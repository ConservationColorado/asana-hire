package org.conservationco.asanahire.repository

import org.conservationco.asanahire.model.user.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveCrudRepository<User, String> {
    fun findByEmail(email: String): Mono<User>
}

package org.conservationco.asanahire.service

import org.conservationco.asanahire.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
) {

}

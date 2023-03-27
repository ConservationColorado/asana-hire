package org.conservationco.asanahire.service

import org.conservationco.asanahire.model.user.User
import org.conservationco.asanahire.repository.UserRepository
import org.conservationco.asanahire.security.AuthProvider
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val logger = Logger.getLogger(UserService::class.qualifiedName)

    internal fun onAuthenticationSuccess(authentication: Authentication): Mono<Void> {
        val principal = authentication.principal as DefaultOidcUser
        val provider = (authentication as OAuth2AuthenticationToken)
            .authorizedClientRegistrationId
            .uppercase(Locale.getDefault())
        return userRepository
            .findByEmail(principal.email)
            .doOnNext { user -> logger.info("Retrieved user: $user") }
            .flatMap {
                if (it != null) {
                    handleExistingUser(provider, principal, it)
                } else {
                    saveUserFromOAuth2(provider, principal)
                }
            }
    }

    private fun handleExistingUser(
        provider: String,
        principal: DefaultOidcUser,
        existingUser: User
    ): Mono<Void> {
        // Update the existing user object with the latest OAuth2 authentication data
        val updatedUser = principal.toUser(provider)
        updatedUser.id = existingUser.id
        return userRepository.save(updatedUser).then()
    }

    private fun saveUserFromOAuth2(provider: String, oidcUser: DefaultOidcUser): Mono<Void> {
        val user = oidcUser.toUser(provider)
        return userRepository
            .save(user)
            .doOnNext { logger.log(Level.INFO, "Saving new user from OAuth2") }
            .then()
    }

}

private fun DefaultOidcUser.toUser(provider: String): User =
    User(
        id = subject,
        name = givenName,
        email = email,
        picture = picture,
        provider = AuthProvider.valueOf(provider)
    )

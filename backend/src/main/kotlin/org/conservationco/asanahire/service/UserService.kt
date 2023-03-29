package org.conservationco.asanahire.service

import org.conservationco.asanahire.model.user.User
import org.conservationco.asanahire.repository.UserRepository
import org.conservationco.asanahire.security.valueOfIgnoreCase
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*
import java.util.logging.Logger

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val logger = Logger.getLogger(UserService::class.qualifiedName)

    internal fun onAuthenticationSuccess(authentication: Authentication): Mono<Void> {
        val provider = (authentication as OAuth2AuthenticationToken).authorizedClientRegistrationId
        val newUser = (authentication.principal as DefaultOidcUser).toUser(provider)
        return userRepository
            .findByEmail(newUser.email)
            .switchIfEmpty(saveNewUser(newUser))
            .flatMap { existingUser -> handleUser(newUser, existingUser) }
            .then()
            .onErrorResume { handleSaveError(it) }
    }

    private fun handleSaveError(it: Throwable): Mono<Void> {
        logger.severe("An error occurred while handling user: ${it.message})")
        return Mono.empty()
    }

    private fun saveNewUser(user: User) =
        saveUser(user, "Saved new user from ${user.provider} OAuth2")

    private fun saveUser(user: User, message: String) =
        userRepository
            .save(user)
            .doOnSuccess { logger.info(message) }

    private fun handleUser(newUser: User, existingUser: User): Mono<User> {
        return if (existingUser != newUser) {
            saveUser(newUser, "Updated existing user with ${existingUser.provider} OAuth2")
        } else {
            Mono.empty()
        }
    }

}

private fun DefaultOidcUser.toUser(provider: String) =
    User(
        name = givenName,
        email = email,
        picture = picture,
        provider = valueOfIgnoreCase(provider.uppercase(Locale.getDefault()))
    )

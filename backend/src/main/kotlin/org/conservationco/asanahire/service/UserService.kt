package org.conservationco.asanahire.service

import org.conservationco.asanahire.model.user.User
import org.conservationco.asanahire.repository.UserRepository
import org.conservationco.asanahire.security.AuthProvider
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.stereotype.Service
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val logger = Logger.getLogger(UserService::class.qualifiedName)

    internal fun findByUsername(username: String): User? = userRepository.findByEmail(username)

    internal fun onAuthenticationSuccess(authentication: Authentication) {
        val principal = authentication.principal as DefaultOidcUser
        val provider = (authentication as OAuth2AuthenticationToken)
            .authorizedClientRegistrationId
            .uppercase(Locale.getDefault())
        val existingUser = findByUsername(principal.email)
        if (existingUser != null) {
            handleExistingUser(provider, principal, existingUser)
        } else {
            saveUserFromOAuth2(provider, principal)
        }
    }

    private fun handleExistingUser(
        provider: String,
        principal: DefaultOidcUser,
        existingUser: User
    ) {
        if (provider == AuthProvider.LOCAL.toString()) {
            transferUserToOAuth2Provider(provider, principal, existingUser)
        } else if (provider != existingUser.provider.toString()) {
            logger.log(Level.SEVERE, "Existing user tried to log in with a different OAuth2 provider.")
            throw OAuth2AuthenticationException("invalid_request")
        }
    }

    private fun saveUserFromOAuth2(
        provider: String,
        oidcUser: DefaultOidcUser
    ): User {
        logger.log(Level.INFO, "Saving new user from OAuth2")
        val user = oidcUser.toUser(provider)
        return userRepository.save(user)
    }

    private fun transferUserToOAuth2Provider(
        provider: String,
        oidcUser: DefaultOidcUser,
        existingUser: User
    ): User {
        logger.log(Level.INFO, "Updating existing local user to use OAuth2 provider")
        val updatedUser = oidcUser.toUser(provider)
        userRepository.delete(existingUser)
        return userRepository.save(updatedUser)
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

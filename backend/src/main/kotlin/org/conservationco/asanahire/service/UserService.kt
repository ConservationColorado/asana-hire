package org.conservationco.asanahire.service

import org.conservationco.asanahire.model.user.User
import org.conservationco.asanahire.model.user.valueOfIgnoreCase
import org.conservationco.asanahire.repository.UserRepository
import org.conservationco.asanahire.util.handleSaveError
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*
import java.util.logging.Logger

@Service
class UserService(
    private val userRepository: UserRepository,
    private val clientService: ReactiveOAuth2AuthorizedClientService,
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
            .onErrorResume { handleSaveError(logger, it) }
    }

    internal fun getCurrentlyAuthenticatedAuthorizedClient() =
        getCurrentAuthentication()
            .flatMap { validateAuthenticationAsOAuth2(it) }
            .flatMap {
                clientService
                    .loadAuthorizedClient<OAuth2AuthorizedClient>(it.authorizedClientRegistrationId, it.name)
            }

    internal fun getCurrentlyAuthenticatedUserEmail() =
        oAuth2AuthenticationToken()
            .map {
                val principal = it.principal as OAuth2User
                principal.attributes["email"] as String
            }

    private fun saveNewUser(user: User) =
        saveUser(user, "Saved new user from ${user.provider} OAuth2")

    private fun saveUser(user: User, message: String) =
        userRepository
            .save(user)
            .doOnSuccess { logger.info(message) }

    private fun handleUser(newUser: User, existingUser: User) =
        if (existingUser != newUser)
            saveUser(newUser, "Updated existing user with ${existingUser.provider} OAuth2")
        else Mono.empty()

    private fun oAuth2AuthenticationToken(): Mono<OAuth2AuthenticationToken> =
        getCurrentAuthentication()
            .flatMap { validateAuthenticationAsOAuth2(it) }

    private fun validateAuthenticationAsOAuth2(auth: Authentication?) =
        if (auth != null && auth.isAuthenticated && auth is OAuth2AuthenticationToken)
            Mono.just(auth)
        else Mono.error(OAuth2AuthenticationException("User not authenticated or OAuth2 token not found!"))

}

private fun DefaultOidcUser.toUser(provider: String) =
    User(
        name = givenName,
        email = email,
        picture = picture,
        provider = valueOfIgnoreCase(provider.uppercase(Locale.getDefault()))
    )

private fun getCurrentAuthentication() =
    ReactiveSecurityContextHolder
        .getContext()
        .map(SecurityContext::getAuthentication)

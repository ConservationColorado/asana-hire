package org.conservationco.asanahire.security

import org.conservationco.asanahire.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OAuth2LoginSuccessHandler(
    private val authorizedClientService: ReactiveOAuth2AuthorizedClientService,
    private val userService: UserService
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        userService.onAuthenticationSuccess(authentication)
        return authorizedClientService
            .loadAuthorizedClient<OAuth2AuthorizedClient>(
                (authentication as OAuth2AuthenticationToken).authorizedClientRegistrationId,
                authentication.name
            )
            .flatMap { authorizedClient -> webFilterExchange.addAccessTokenCookie(authorizedClient) }
    }

    private fun WebFilterExchange.addAccessTokenCookie(
        authorizedClient: OAuth2AuthorizedClient
    ): Mono<Void> {
        // Create a cookie with the access token
        val cookie =
            ResponseCookie
                .from("access_token", authorizedClient.accessToken.tokenValue)
                .httpOnly(true)
                .domain("http://localhost:3000")

        // Add the cookie to the response headers
        val headers = exchange.response.headers
        headers.add("Set-Cookie", cookie.toString())

        val response = exchange.response
        response.statusCode = HttpStatus.TEMPORARY_REDIRECT
        response.headers.location = URI.create("http://localhost:3000")

        return Mono.empty()
    }

}

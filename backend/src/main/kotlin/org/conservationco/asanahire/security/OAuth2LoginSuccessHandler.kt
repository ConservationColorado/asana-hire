package org.conservationco.asanahire.security

import org.conservationco.asanahire.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI

@Component
class OAuth2LoginSuccessHandler(
    private val userService: UserService,
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        redirectUser(webFilterExchange.exchange.response)
        return userService.onAuthenticationSuccess(authentication)
    }

    private fun redirectUser(response: ServerHttpResponse) {
        response.statusCode = HttpStatus.TEMPORARY_REDIRECT
        response.headers.location = URI.create("http://localhost:3000")
    }

}

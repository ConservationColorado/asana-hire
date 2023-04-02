package org.conservationco.asanahire.security

import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseCookie
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class CsrfFilter : WebFilter {

    @Bean
    fun csrfTokenRepository(): ServerCsrfTokenRepository =
        CookieServerCsrfTokenRepository.withHttpOnlyFalse()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val csrfCookieName = "XSRF-TOKEN"
        val key = CsrfToken::class.java.name
        val csrfToken: Mono<CsrfToken> = exchange.getAttribute(key) ?: Mono.empty()
        return csrfToken.doOnSuccess { token ->
            val existingCookie = exchange.request.cookies.getFirst(csrfCookieName)
            if (existingCookie == null) {
                val cookie = ResponseCookie.from(csrfCookieName, token.token)
                    .maxAge(Duration.ofHours(1))
                    .httpOnly(false)
                    .path("/")
                    .build()
                exchange.response.cookies.add(csrfCookieName, cookie)
            }
        }.then(chain.filter(exchange))
    }

}

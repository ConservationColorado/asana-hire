package org.conservationco.asanahire.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
class CsrfConfiguration {

    @Bean
    internal fun csrfTokenRepository() =
        CookieServerCsrfTokenRepository.withHttpOnlyFalse()

    @Bean
    internal fun csrfDelegateHandleFunction() =
        XorServerCsrfTokenRequestAttributeHandler()

    @Component
    class CsrfWebFilter : WebFilter {
        override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
            val csrfToken: Mono<CsrfToken> = exchange.getAttributeOrDefault(CsrfToken::class.java.name, Mono.empty())
            return csrfToken
                .doOnSuccess {
                    // from https://docs.spring.io/spring-security/reference/5.8/migration/reactive.html
                }
                .then(chain.filter(exchange))
        }
    }

}

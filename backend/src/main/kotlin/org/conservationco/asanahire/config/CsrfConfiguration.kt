package org.conservationco.asanahire.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.security.web.server.csrf.XorServerCsrfTokenRequestAttributeHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
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
    internal class CsrfWebFilter : WebFilter {
        override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
            val csrfToken: Mono<CsrfToken> = exchange.getAttributeOrDefault(CsrfToken::class.java.name, Mono.empty())
            return csrfToken
                .doOnSuccess {
                    // from https://docs.spring.io/spring-security/reference/5.8/migration/reactive.html
                }
                .then(chain.filter(exchange))
        }
    }

    @Component
    class CsrfServerWebExchangeMatcher : ServerWebExchangeMatcher {
        override fun matches(exchange: ServerWebExchange): Mono<ServerWebExchangeMatcher.MatchResult> {
            val path = exchange.request.uri.path
            val method = exchange.request.method
            return if (isCsrfDisabledUrl(path) || isCsrfIgnoredMethod(method)) {
                ServerWebExchangeMatcher.MatchResult.notMatch()
            } else {
                ServerWebExchangeMatcher.MatchResult.match()
            }
        }
    }

}

internal val csrfIgnoredMethods = arrayOf(
    HttpMethod.GET,
    HttpMethod.HEAD,
    HttpMethod.TRACE,
    HttpMethod.OPTIONS
)

internal val csrfDisabledPatterns = arrayOf(
    asanaWebhookCreatePath
)

internal fun isCsrfDisabledUrl(path: String) = doesPathMatch(path, csrfDisabledPatterns)
internal fun isCsrfIgnoredMethod(method: HttpMethod) = csrfIgnoredMethods.contains(method)

package org.conservationco.asanahire.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.csrf.ServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestHandler
import org.springframework.web.cors.reactive.CorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration(
    private val successHandler: ServerAuthenticationSuccessHandler,
    private val csrfTokenRepository: ServerCsrfTokenRepository,
    private val corsConfiguration: CorsConfigurationSource,
    private val csrfDelegate: ServerCsrfTokenRequestHandler,
) {

    @Bean
    internal fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange { authSpec ->
                authSpec
                    .pathMatchers("/", "/login", "/error").permitAll()
                    .anyExchange().authenticated()
            }
            .cors { corsSpec ->
                corsSpec.configurationSource(corsConfiguration)
            }
            .csrf { csrfSpec ->
                csrfSpec
                    .csrfTokenRepository(csrfTokenRepository)
                    .csrfTokenRequestHandler(csrfDelegate::handle)
            }
            .oauth2Login { oAuth2Spec ->
                oAuth2Spec
                    .authenticationSuccessHandler(successHandler)
            }
        return http.build()
    }

}

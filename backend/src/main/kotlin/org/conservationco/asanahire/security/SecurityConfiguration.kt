package org.conservationco.asanahire.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository

@Configuration
@EnableWebFluxSecurity
class SecurityConfiguration {

    @Autowired
    private lateinit var successHandler: ServerAuthenticationSuccessHandler

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http
            .authorizeExchange { authSpec ->
                authSpec
                    .anyExchange()
                    .authenticated()
            }
            .logout { logoutSpec ->
                logoutSpec
                    .logoutUrl("/")
            }
            .csrf { csrfSpec ->
                csrfSpec
                    .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
            }
            .oauth2Login { oAuth2LoginSpec ->
                oAuth2LoginSpec
                    .authenticationSuccessHandler(successHandler)
            }
        return http.build()
    }

}

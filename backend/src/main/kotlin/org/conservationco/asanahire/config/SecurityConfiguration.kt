package org.conservationco.asanahire.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

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
            .cors { corsSpec ->
                corsSpec.configurationSource(corsConfigurationSource())
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

    private fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:3000")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowCredentials = true
        source.registerCorsConfiguration("/**", config)
        return source
    }

}

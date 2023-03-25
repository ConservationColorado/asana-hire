package org.conservationco.asanahire.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): OAuth2LoginConfigurer<HttpSecurity> {
        return http
            .authorizeHttpRequests { authConfig ->
                authConfig
                    .requestMatchers("/", "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .logout { logoutConfig ->
                logoutConfig
                    .logoutSuccessUrl("/")
                    .permitAll()
            }
            .csrf { csrfConfig ->
                csrfConfig
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            }
            .exceptionHandling { errorConfig ->
                errorConfig
                    .authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .oauth2Login()
    }

}

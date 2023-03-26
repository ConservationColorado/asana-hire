package org.conservationco.asanahire.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Autowired
    private lateinit var successHandler: OAuth2LoginSuccessHandler

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
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
            .oauth2Login { oauth2Config ->
                oauth2Config.successHandler(successHandler)
            }
        return http.build()
    }

}

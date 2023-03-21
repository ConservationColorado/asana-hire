package org.conservationco.asanahire.config

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.oauth2.Oauth2Scopes
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Value("\${spring.security.oauth2.client.registration.google.client-id}")
    private lateinit var googleClientId: String

    @Value("\${spring.security.oauth2.client.registration.google.client-secret}")
    private lateinit var googleClientSecret: String

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth -> auth.requestMatchers("/auth/google").permitAll() }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .cors().and().csrf().disable()
            .build()
    }

    @Bean
    fun getGoogleAuthorizationCodeFlow(): GoogleAuthorizationCodeFlow {
        val scopes = listOf(
            Oauth2Scopes.USERINFO_EMAIL,
            Oauth2Scopes.USERINFO_PROFILE,
            Oauth2Scopes.OPENID
        )
        return GoogleAuthorizationCodeFlow
            .Builder(
                transport(),
                jsonFactory(),
                googleClientId,
                googleClientSecret,
                scopes
            )
            .build()
    }

    @Bean
    fun transport() = NetHttpTransport()

    @Bean
    fun jsonFactory() = GsonFactory.getDefaultInstance()!!

    @Bean
    fun tokenVerifier() =
        GoogleIdTokenVerifier
            .Builder(
                transport(),
                jsonFactory()
            )
            .setAudience(listOf(googleClientId))
            .build()

}

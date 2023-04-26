package org.conservationco.asanahire.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfiguration {

    @Value("\${client-base-url}")
    private lateinit var clientUrl: String

    @Bean
    internal fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = listOf(clientUrl)
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowCredentials = true
        config.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type", "X-XSRF-TOKEN")
        source.registerCorsConfiguration("/**", config)
        return source
    }

}

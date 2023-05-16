package org.conservationco.asanahire.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class CorsConfiguration {

    @Value("\${client-base-url}")
    private lateinit var clientUrl: String

    /**
     * A more restrictive CORS configuration applied to most endpoints served by this application.
     */
    private val globalCorsConfig = CorsConfiguration()

    /**
     * A more permissive CORS configuration for webhook receives that allows only POST requests but from any origin.
     */
    private val webhookCorsConfig = CorsConfiguration()

    @PostConstruct
    private fun initCorsConfig() {
        globalCorsConfig.apply {
            allowCredentials = true
            allowedOrigins = listOf(clientUrl)
            allowedMethods = listOf(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
            allowedHeaders = listOf(
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "X-XSRF-TOKEN"
            )
        }
        webhookCorsConfig.apply {
            allowedOrigins = listOf("*")
            allowedMethods = listOf("POST")
            allowedHeaders = listOf("X-Hook-Secret")
        }
    }


    @Bean
    internal fun corsConfigurationSource() =
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(asanaWebhookCreatePath, webhookCorsConfig)
            registerCorsConfiguration("/**", globalCorsConfig)
        }

}

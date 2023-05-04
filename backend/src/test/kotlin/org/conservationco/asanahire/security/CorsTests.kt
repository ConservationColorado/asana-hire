package org.conservationco.asanahire.security

import org.conservationco.asanahire.config.webhookCreatePath
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class CorsTests(
    @Autowired private val client: WebTestClient,
    @Value("\${client-base-url}") private val allowedUrl: String,
    @Value("\${server-base-url}") private val serverUrl: String,
) {

    @Test
    internal fun `should allow requests originating from allowed url`() {
        client
            .get()
            .uri("$serverUrl/user/me")
            .header(HttpHeaders.ORIGIN, allowedUrl)
            .exchange()
            .expectStatus().isFound
            .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowedUrl)
    }

    @Test
    internal fun `should not allow requests not originating from allowed url`() {
        client
            .get()
            .uri("$serverUrl/user/me")
            .header(HttpHeaders.ORIGIN, "https://evil.com")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
    }

    @Test
    internal fun `should support preflight requests`() {
        client
            .options()
            .uri("$serverUrl/jobs")
            .header(HttpHeaders.ORIGIN, allowedUrl)
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowedUrl)
            .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS")
    }

    @Test
    internal fun `should reject invalid preflight requests`() {
        client
            .method(HttpMethod.OPTIONS)
            .uri("$serverUrl/jobs")
            .header(HttpHeaders.ORIGIN, "https://evil.com")
            .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PATCH")
            .exchange()
            .expectStatus().isForbidden
            .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
            .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)
            .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS)
            .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_MAX_AGE)
    }

    @Test
    internal fun `should allow POST requests from any origin at webhook create endpoint`() {
        client
            .method(HttpMethod.POST)
            .uri("$serverUrl/$webhookCreatePath")
            .header(HttpHeaders.ORIGIN, "https://website.com")
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    @Test
    internal fun `should allow X-Hook-Secret header at webhook create endpoint`() {
        client
            .method(HttpMethod.POST)
            .uri("$serverUrl/$webhookCreatePath")
            .header("X-Hook-Secret", "12345")
            .exchange()
            .expectStatus().is2xxSuccessful
    }

}

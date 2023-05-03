package org.conservationco.asanahire.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class CsrfTests(
    @Autowired private val client: WebTestClient,
) {

    @Test
    internal fun `should allow POST with valid CSRF`() {
        client
            .mutateWith(mockOidcUser())
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/logout")
            .exchange()
            .expectStatus().is3xxRedirection
    }

    @Test
    internal fun `should deny POST without valid CSRF`() {
        client
            .mutateWith(mockOidcUser())
            .post()
            .uri("/logout")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    internal fun `should allow POST request to webhook create endpoint with valid CSRF`() {
        client
            .mutateWith(SecurityMockServerConfigurers.csrf())
            .post()
            .uri("/webhook/create")
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    @Test
    internal fun `should allow POST request to webhook create endpoint without valid CSRF`() {
        client
            .post()
            .uri("/webhook/create")
            .exchange()
            .expectStatus().is2xxSuccessful
    }

    @Test
    internal fun `should deny DELETE request to webhook delete endpoint without valid CSRF`() {
        client
            .delete()
            .uri("/webhook/delete")
            .exchange()
            .expectStatus().isForbidden
    }

}

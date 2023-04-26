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
internal class AuthenticationTests(
    @Autowired private val client: WebTestClient,
) {

    @Test
    internal fun `should allow access to public pages`() {
        client
            .get()
            .uri("/login")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    internal fun `should redirect unauthenticated requests`() {
        client
            .get()
            .uri("/user/me")
            .exchange()
            .expectStatus().is3xxRedirection
    }

    @Test
    internal fun `should allow access to protected pages with authentication`() {
        client
            .mutateWith(mockOidcUser())
            .get()
            .uri("/user/me")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    internal fun `should deny form login access`() {
        client
            .post()
            .uri("/login")
            .bodyValue(mapOf("username" to "invalid", "password" to "invalid"))
            .exchange()
            .expectStatus().isForbidden
    }

}

internal fun mockOidcUser(): SecurityMockServerConfigurers.OidcLoginMutator =
    SecurityMockServerConfigurers
        .mockOidcLogin()
        .idToken { it.claim("name", "Mock User") }

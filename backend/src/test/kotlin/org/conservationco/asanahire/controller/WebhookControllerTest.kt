package org.conservationco.asanahire.controller

import org.conservationco.asanahire.config.asanaWebhookCreatePath
import org.conservationco.asanahire.config.webhookSecretHeader
import org.conservationco.asanahire.config.webhookSignatureHeader
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class WebhookControllerTest(
    @Autowired private val client: WebTestClient,
) {

    @Test
    fun `should allow X-Hook-Signature header`() {
        client
            .options()
            .uri(asanaWebhookCreatePath)
            .header(webhookSignatureHeader, "54321")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `should require a request body on webhook create request`() {
        client
            .post()
            .uri(asanaWebhookCreatePath)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `should not allow calls with neither X-Hook-Secret nor X-Hook-Signature headers`() {
        client
            .post()
            .uri(asanaWebhookCreatePath)
            .bodyValue("")
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `should reject request when X-Hook-Signature header is not derived from a previously established shared secret`() {
        // Setup with a mock secret
        client
            .post()
            .uri(asanaWebhookCreatePath)
            .header(webhookSecretHeader, "12345")
            .bodyValue("")
            .exchange()

        // Send an event with an invalid signature
        client
            .post()
            .uri(asanaWebhookCreatePath)
            .header(webhookSignatureHeader, "54321")
            .bodyValue(
                """
                {
                    "foo": ["bar"]
                }
            """.trimIndent()
            )
            .exchange()
            .expectStatus().is4xxClientError
    }

    @Test
    fun `should return X-Hook-Secret header on webhook handshake initiation`() {
        client
            .post()
            .uri(asanaWebhookCreatePath)
            .header(webhookSecretHeader, "12345")
            .bodyValue("")
            .exchange()
            .expectStatus().isNoContent
            .expectHeader().valueMatches(webhookSecretHeader, "12345")
    }

}

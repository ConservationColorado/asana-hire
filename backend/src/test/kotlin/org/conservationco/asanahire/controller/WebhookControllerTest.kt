package org.conservationco.asanahire.controller

import org.conservationco.asanahire.config.asanaWebhookPath
import org.conservationco.asanahire.config.webhookSecretHeader
import org.conservationco.asanahire.config.webhookSignatureHeader
import org.conservationco.asanahire.util.computeHmac256Signature
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
    fun `pre-handshake, should allow X-Hook-Secret header`() {
        client
            .options()
            .uri(asanaWebhookPath)
            .header(webhookSecretHeader, "12345")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `pre-handshake, should allow X-Hook-Signature header`() {
        client
            .options()
            .uri(asanaWebhookPath)
            .header(webhookSignatureHeader, "54321")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `pre-handshake, should allow requests without a body`() {
        client
            .post()
            .uri(asanaWebhookPath)
            .exchange()
            .expectStatus().isNoContent

        client
            .post()
            .uri(asanaWebhookPath)
            .bodyValue("")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `pre-handshake, should allow calls with neither X-Hook-Secret nor X-Hook-Signature headers`() {
        client
            .post()
            .uri(asanaWebhookPath)
            .bodyValue("")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `mid-handshake, should return X-Hook-Secret header on initiation`() {
        client
            .post()
            .uri(asanaWebhookPath)
            .header(webhookSecretHeader, "12345")
            .bodyValue("")
            .exchange()
            .expectStatus().isNoContent
            .expectHeader().valueMatches(webhookSecretHeader, "12345")
    }

    @Test
    fun `post-handshake, should reject request when X-Hook-Signature header is not derived from shared secret`() {
        val secret = "12345"
        val body = """
            {
              "events": [
              
              ]
            }
        """.trimIndent()


        // Setup with a mock secret
        client
            .post()
            .uri(asanaWebhookPath)
            .header(webhookSecretHeader, secret)
            .bodyValue("")
            .exchange()

        // Send an event with an invalid signature
        client
            .post()
            .uri(asanaWebhookPath)
            .header(webhookSignatureHeader, "This is not the agreed-upon secret!")
            .bodyValue(body)
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `post-handshake, should accept request when X-Hook-Signature header is derived from shared secret`() {
        val secret = "12345"
        val body = """
            {
              "events": [
              
              ]
            }
        """.trimIndent()
        val signature = computeHmac256Signature(secret, body)

        // Setup with a mock secret
        client
            .post()
            .uri(asanaWebhookPath)
            .header(webhookSecretHeader, secret)
            .bodyValue("")
            .exchange()

        // Send an event with a valid signature
        client
            .post()
            .uri(asanaWebhookPath)
            .header(webhookSignatureHeader, signature)
            .bodyValue(body)
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `post-handshake, should allow heartbeat requests with empty body and no signature after handshake`() {
        val secret = "12345"

        // Setup with a mock secret
        client
            .post()
            .uri(asanaWebhookPath)
            .header(webhookSecretHeader, secret)
            .bodyValue("")
            .exchange()

        // Send an event with no signature and an empty request body
        client
            .post()
            .uri(asanaWebhookPath)
            .bodyValue("")
            .exchange()
            .expectStatus().isNoContent
    }

}

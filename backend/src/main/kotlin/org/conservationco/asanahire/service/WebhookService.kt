package org.conservationco.asanahire.service

import org.conservationco.asanahire.config.webhookSecretHeader
import org.conservationco.asanahire.util.isSignedBySecret
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class WebhookService(
    private val eventService: EventService,
) {

    /**
     * Stores `X-Hook-Secret` shared secret values given in the initial webhook handshake. Values stored in this set are
     * used to validate HMAC SHA256 signatures given in webhook events.
     */
    private val secrets = ConcurrentHashMap.newKeySet<String>()

    internal fun handleWebhookRequest(secret: String?, signature: String?, body: String?): ResponseEntity<String> =
        if (isNewSecret(secret)) processNewSecret(secret)
        else if (signature != null) validateRequestBody(signature, body)
        else ResponseEntity.noContent().build()

    private fun processNewSecret(secret: String?): ResponseEntity<String> {
        secrets.add(secret)
        val responseHeaders = HttpHeaders()
        responseHeaders[webhookSecretHeader] = secret
        return ResponseEntity.noContent().headers(responseHeaders).build()
    }

    private fun validateRequestBody(signature: String, body: String?): ResponseEntity<String> =
        if (isSignedByAnyKnownSecrets(signature, body) && !body.isNullOrEmpty()) {
            eventService.processEvents(body)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }

    private fun isSignedByAnyKnownSecrets(signature: String, body: String?) =
        secrets.any { isSignedBySecret(it, signature, body) }

    private fun isNewSecret(secret: String?) =
        !secret.isNullOrBlank() && secret.isNotEmpty()

}

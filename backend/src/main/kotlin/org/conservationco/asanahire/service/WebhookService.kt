package org.conservationco.asanahire.service

import org.conservationco.asanahire.config.webhookSecretHeader
import org.conservationco.asanahire.util.isSignedBySecret
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class WebhookService {

    /**
     * Stores `X-Hook-Secret` values given in the initial webhook handshake. These secrets are used to validate signed
     * HMAC SHA256 signatures given in webhook events.
     */
    private val secrets = ConcurrentHashMap.newKeySet<String>()

    internal fun handleWebhookRequest(
        secret: String?,
        signature: String?,
        body: String?
    ): ResponseEntity<String> =
        if (secret != null) processSecret(secret)
        else if (signature == null) ResponseEntity.noContent().build()
        else if (isSignedByAnyKnownSecrets(signature, body)) processEvents(body)
        else ResponseEntity.badRequest().build()

    private fun processSecret(secret: String): ResponseEntity<String> =
        if (secret.isBlank() || secret.isEmpty()) ResponseEntity.badRequest().build()
        else {
            secrets.add(secret)
            val responseHeaders = HttpHeaders()
            responseHeaders[webhookSecretHeader] = secret
            ResponseEntity.noContent().headers(responseHeaders).build()
        }

    private fun processEvents(body: String?): ResponseEntity<String> =
        if (body.isNullOrEmpty()) ResponseEntity.badRequest().build()
        else {
            ResponseEntity.ok().build()
        }

    private fun isSignedByAnyKnownSecrets(signature: String, body: String?) =
        secrets.any { isSignedBySecret(it, signature, body) }

}

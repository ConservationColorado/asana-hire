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

    /**
     * Control flow
     *   - if x-hook secret exists, then save it and return it
     *   - if it does not exist, check if the x-hook-signature exists
     *      - if it does not exist, return 4xx
     *      - if it does exist, validate the body of the request against the X-Hook-Secret and compare to given
     *        X-Hook-Signature, which is an HMAC SHA256 signature
     *           - if it matches, process the data
     *           - if it does not match, return 4xx
     */
    internal fun processWebhookRequest(
        secret: String?,
        signature: String?,
        body: String?
    ): ResponseEntity<String> = when {
        secret != null -> {
            processSecret(secret)
            val responseHeaders = HttpHeaders()
            responseHeaders[webhookSecretHeader] = secret
            ResponseEntity.noContent().headers(responseHeaders).build()
        }

        signature == null -> {
            ResponseEntity.noContent().build()
        }

        isSignedByAnyKnownSecrets(signature, body) -> {
            if (!body.isNullOrEmpty()) processEvents(body)
            ResponseEntity.ok().build()
        }

        else -> ResponseEntity.badRequest().build()
    }

    private fun processSecret(secret: String) {
        if (secret.isNotBlank() && secret.isNotEmpty()) secrets.add(secret)
    }

    private fun processEvents(body: String) {
        // todo
    }

    private fun isSignedByAnyKnownSecrets(signature: String, body: String?) =
        secrets.any { isSignedBySecret(it, signature, body) }

}

package org.conservationco.asanahire.service

import org.conservationco.asanahire.config.webhookSecretHeader
import org.conservationco.asanahire.util.isSignedBySecret
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
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

    internal fun handleWebhookRequest(secret: String?, signature: String?, body: String?): Mono<out ResponseEntity<*>> =
        if (isNewSecret(secret)) processNewSecret(secret)
        else if (signature != null) validateRequestBody(signature, body)
        else Mono.just(ResponseEntity.noContent().build<String>())

    private fun processNewSecret(secret: String?): Mono<ResponseEntity<*>> {
        secrets.add(secret)
        val responseHeaders = HttpHeaders()
        responseHeaders[webhookSecretHeader] = secret
        return Mono.just(ResponseEntity.noContent().headers(responseHeaders).build<String>())
    }

    private fun validateRequestBody(signature: String, body: String?): Mono<ResponseEntity<String>> =
        if (isSignedByAnyKnownSecrets(signature, body) && !body.isNullOrEmpty()) {
            eventService.processEvents(body)
        } else {
            Mono.just(ResponseEntity.badRequest().build<String>())
        }

    private fun isSignedByAnyKnownSecrets(signature: String, body: String?) =
        secrets.any { isSignedBySecret(it, signature, body) }

    private fun isNewSecret(secret: String?) =
        !secret.isNullOrBlank() && secret.isNotEmpty()

}

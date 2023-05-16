package org.conservationco.asanahire.controller

import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.config.asanaWebhookCreatePath
import org.conservationco.asanahire.config.webhookSecretHeader
import org.conservationco.asanahire.config.webhookSignatureHeader
import org.conservationco.asanahire.model.job.Job
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


@RestController
class WebhookController {

    /**
     * Stores `X-Hook-Secret` values given in the initial webhook handshake. These secrets are used to validate signed
     * HMAC SHA256 signatures given in webhook events.
     */
    private val secrets = ConcurrentHashMap.newKeySet<String>()

    /**
     * Control flow
     *        - if x-hook secret exists, then save it and return it
     *        - if it does not exist, check if the x-hook-signature exists
     *           - if it does not exist, return 400
     *           - if it does exist, validate the body of the request against the x-hook-secret and compare to given
     *             x-hook-signature, which is an HMAC SHA256 signature
     *                - if it matches, process the data
     *                - if it does not match, return 400
     * Other requirements
     *   - must be able to delete and recreate webhooks when the application restarts
     *   - catch a bad request to delete and recreate webhooks
     *   - iterate over x-hook-secrets to find a match
     */
    // todo move logic to a service layer
    // todo if the secret exists, iterate over existing secrets
    // todo handle events
    // todo validate if events match the source expected from the stored secret
    @PostMapping(asanaWebhookCreatePath)
    fun asanaWebhookEntrypoint(
        @RequestHeader(webhookSecretHeader) secret: String?,
        @RequestHeader(webhookSignatureHeader) signature: String?,
        @RequestBody body: String?
    ): Mono<ResponseEntity<String>> {
        val result: ResponseEntity<String> = if (secret != null) {
            // this is a new webhook
            secrets.add(secret)
            val responseHeaders = HttpHeaders().apply { set(webhookSecretHeader, secret) }
            ResponseEntity
                .noContent()
                .headers(responseHeaders)
                .build()
        } else if (signature != null) {
            // this could be a request made from an existing webhook, but we don't yet know if we should trust it
            // valid events (the request body) are signed with one of the secrets stored (it could be any!)
            // check if any of our secrets produces a HMAC SHA256 digest that matches the signature
            val matches = secrets.any { isTrusted(it, signature, body) }
            if (matches) {
                // process the events
            }
            else {
                // warn untrusted
            }
            ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build()
        } else {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build()
        }
        return Mono.just(result)
    }

    @PostMapping("/webhook/new")
    fun newWebhookRequest(@RequestBody job: Job): String = asanaContext { return "" }

    @DeleteMapping("/webhook/delete")
    fun deleteWebhook(@RequestBody webhookId: String) = asanaContext {}

}

/**
 * Returns true if [body] is signed with the given secret.
 */
private fun isTrusted(
    secret: String,
    givenSignature: String,
    body: String?
): Boolean {
    val hmacSha256: Mac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    hmacSha256.init(secretKey)

    val digest: ByteArray = hmacSha256.doFinal(body?.toByteArray())
    val calculatedSignature: String = Base64.getEncoder().encodeToString(digest)

    return calculatedSignature == givenSignature
}

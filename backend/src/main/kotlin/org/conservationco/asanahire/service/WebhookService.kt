package org.conservationco.asanahire.service

import org.conservationco.asanahire.config.webhookSecretHeader
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

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
            secrets.add(secret)
            val responseHeaders = HttpHeaders()
            responseHeaders[webhookSecretHeader] = secret
            ResponseEntity.noContent().headers(responseHeaders).build()
        }

        signature != null && isSignedByAnyKnownSecrets(signature, body) -> {
            processEvents(body)
            ResponseEntity.noContent().build()
        }

        else -> ResponseEntity.badRequest().build()
    }

    private fun processEvents(body: String?) {
        TODO("Not yet implemented")
    }

    private fun isSignedByAnyKnownSecrets(signature: String, body: String?) =
        secrets.any { isSignedBySecret(it, signature, body) }

}

/**
 * Returns true if [body] is signed with the given secret.
 */
private fun isSignedBySecret(
    secret: String, givenSignature: String, body: String?
): Boolean {
    val hmacSha256: Mac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    hmacSha256.init(secretKey)

    val digest: ByteArray = hmacSha256.doFinal(body?.toByteArray())
    val computedSignature: String = Base64.getEncoder().encodeToString(digest)

    return computedSignature == givenSignature
}

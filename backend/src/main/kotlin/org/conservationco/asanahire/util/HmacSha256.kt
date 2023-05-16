package org.conservationco.asanahire.util

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Returns true if [data] is signed with the given [secret].
 */
internal fun isSignedBySecret(secret: String, givenSignature: String, data: String?): Boolean {
    val hmacSha256: Mac = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    hmacSha256.init(secretKey)
    val digest: ByteArray = hmacSha256.doFinal(data?.toByteArray())
    val computedSignature: String = Base64.getEncoder().encodeToString(digest)
    return computedSignature == givenSignature
}

internal fun computeHmac256Signature(secret: String, data: String): String {
    val algorithm = "HmacSHA256"
    val hmacKey = SecretKeySpec(secret.toByteArray(), algorithm)
    val hmac = Mac.getInstance(algorithm)
    hmac.init(hmacKey)
    val signature = hmac.doFinal(data.toByteArray())
    return Base64.getEncoder().encodeToString(signature)
}

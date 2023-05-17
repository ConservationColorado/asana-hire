package org.conservationco.asanahire.util

import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Returns true if [data] is signed with the given [secret].
 */
internal fun isSignedBySecret(secret: String, givenSignature: String, data: String?): Boolean {
    val hmacSha256 = Mac.getInstance("HmacSHA256")
    val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    hmacSha256.init(secretKey)
    val digest = hmacSha256.doFinal(data?.toByteArray())
    val computedSignature = HexFormat.of().formatHex(digest)
    return MessageDigest.isEqual(computedSignature.toByteArray(), givenSignature.toByteArray())
}

internal fun computeHmac256Signature(secret: String, data: String): String {
    val algorithm = "HmacSHA256"
    val hmacKey = SecretKeySpec(secret.toByteArray(), algorithm)
    val hmac = Mac.getInstance(algorithm)
    hmac.init(hmacKey)
    val signature = hmac.doFinal(data.toByteArray())
    return HexFormat.of().formatHex(signature)
}

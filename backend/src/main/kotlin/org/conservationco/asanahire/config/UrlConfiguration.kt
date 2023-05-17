package org.conservationco.asanahire.config

import org.springframework.util.AntPathMatcher

internal const val webhookSecretHeader = "X-Hook-Secret"
internal const val webhookSignatureHeader = "X-Hook-Signature"
internal const val asanaWebhookPath = "/webhook/asana"

private val antPathMatcher = AntPathMatcher()

internal val publicPatterns = arrayOf(
    "/login",
    "/error",
    asanaWebhookPath
)

internal fun isPublicUrl(path: String) = doesPathMatch(path, publicPatterns)

internal fun doesPathMatch(path: String, patterns: Array<String>) = patterns.any { antPathMatcher.match(it, path) }

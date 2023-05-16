package org.conservationco.asanahire.config

import org.springframework.util.AntPathMatcher

internal const val webhookSecretHeader = "x-hook-secret"
internal const val webhookSignatureHeader = "x-hook-signature"
internal const val asanaWebhookCreatePath = "/webhook/asana/create"

private val antPathMatcher = AntPathMatcher()

internal val publicPatterns = arrayOf(
    "/login",
    "/error",
    asanaWebhookCreatePath
)

internal fun isPublicUrl(path: String) = doesPathMatch(path, publicPatterns)

internal fun doesPathMatch(path: String, patterns: Array<String>) = patterns.any { antPathMatcher.match(it, path) }

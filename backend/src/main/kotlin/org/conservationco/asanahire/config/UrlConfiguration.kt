package org.conservationco.asanahire.config

import org.springframework.util.AntPathMatcher

private val antPathMatcher = AntPathMatcher()

internal const val webhookCreatePath = "/webhook/create"

internal val publicPatterns = arrayOf(
    "/login",
    "/error",
    webhookCreatePath
)

internal fun isPublicUrl(path: String) = doesPathMatch(path, publicPatterns)

internal fun doesPathMatch(path: String, patterns: Array<String>) = patterns.any { antPathMatcher.match(it, path) }

package org.conservationco.asanahire.util

import java.net.URI
import java.net.URISyntaxException

/**
 * Extracts the top-level domain from a given URL string, replacing any subdomains with a single dot.
 *
 * @param url the URL string to extract the domain from
 * @return The top-level domain name with all subdomains replaced by a single dot, or `null` if the input string is not
 *         a valid URL or no domain name exists
 */
fun extractDomainFromUrl(url: String) =
    try {
        URI(url).host?.removePrefix("www.")
    } catch (e: URISyntaxException) {
        null
    }

fun extractHostnameFromUrl(url: String) =
    try {
        val parsed = extractDomainFromUrl(url) ?: ""
        val lastIdx = parsed.lastIndexOf('.')
        val secondLastIdx = parsed.lastIndexOf('.', lastIdx - 1)
        parsed.substring(secondLastIdx + 1, parsed.length)
    } catch (e: URISyntaxException) {
        null
    }


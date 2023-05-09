package org.conservationco.asanahire.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class UrlParserTest {

    @Test
    fun `should extract domain from URL with path and query parameters`() {
        val url = "https://api.example.com/path/to/resource?param=value#fragment"
        val domain = extractDomainFromUrl(url)
        assertEquals("api.example.com", domain)
    }

    @Test
    fun `should extract domain from URL with only host`() {
        val url = "https://example.com"
        val domain = extractDomainFromUrl(url)
        assertEquals("example.com", domain)
    }

    @Test
    fun `should extract domain from URL with localhost`() {
        val url = "http://localhost:8080/path/to/resource"
        val domain = extractDomainFromUrl(url)
        assertEquals("localhost", domain)
    }

    @Test
    fun `should return null for invalid URL`() {
        val url = "not a valid url"
        val domain = extractDomainFromUrl(url)
        assertNull(domain)
    }

    @Test
    fun `should remove subdomains from valid url`() {
        val url = "https://api.example.com"
        val domain = extractHostnameFromUrl(url)
        assertEquals("example.com", domain)
    }

    @Test
    fun `should remove subdomains from valid url with more than one subdomain`() {
        val url = "https://staging.api.example.com"
        val domain = extractHostnameFromUrl(url)
        assertEquals("example.com", domain)
    }

}

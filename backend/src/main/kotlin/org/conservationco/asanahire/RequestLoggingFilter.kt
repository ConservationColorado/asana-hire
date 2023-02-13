package org.conservationco.asanahire

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RequestLoggingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.info(request.formatRequest())
        filterChain.doFilter(request, response)
    }

    private fun HttpServletRequest.formatRequest(): String =
        "Received $method request to $localName:$localPort$requestURI"
}

package org.conservationco.asanahire.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.conservationco.asanahire.service.UserService
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2LoginSuccessHandler(
    private val userService: UserService
) : SavedRequestAwareAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        response.sendRedirect("http://localhost:3000")
        userService.onAuthenticationSuccess(authentication)
        super.onAuthenticationSuccess(request, response, authentication)
    }

}

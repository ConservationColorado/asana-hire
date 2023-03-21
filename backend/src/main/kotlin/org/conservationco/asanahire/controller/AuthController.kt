package org.conservationco.asanahire.controller

import org.conservationco.asanahire.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["http://localhost:3000"])
class AuthController(
    private val authService: AuthService,
) {

    @RequestMapping("/google")
    fun validateAndExchangeGoogleAuthorizationCode(
        @RequestParam("code") code: String, // todo accept error query parameter
    ): Mono<ResponseEntity<*>> {
        return authService.validateCode(code)
    }

}

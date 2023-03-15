package org.conservationco.asanahire.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

    @PostMapping("/auth/google")
    fun exchangeAuthorizationCode(@RequestParam("code") code: String): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

}

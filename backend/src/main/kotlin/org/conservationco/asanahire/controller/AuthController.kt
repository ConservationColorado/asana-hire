package org.conservationco.asanahire.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@CrossOrigin
class AuthController {

    @RequestMapping("/google")
    fun validateAndExchangeGoogleAuthorizationCode(@RequestParam("code") code: String): ResponseEntity<Any> {
        return ResponseEntity.ok().build()
    }

}

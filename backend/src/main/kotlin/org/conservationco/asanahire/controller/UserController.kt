package org.conservationco.asanahire.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController {

    @GetMapping("/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User): Map<String, *> =
        principal.attributes

}

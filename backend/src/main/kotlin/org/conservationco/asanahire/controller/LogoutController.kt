package org.conservationco.asanahire.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.WebSession

@RestController
@RequestMapping("/oauth2/logout")
class LogoutController {

    @PostMapping
    fun logout(session: WebSession) = session.invalidate()

}

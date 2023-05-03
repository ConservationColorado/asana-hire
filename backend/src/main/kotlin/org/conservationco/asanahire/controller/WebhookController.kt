package org.conservationco.asanahire.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/webhook")
class WebhookController {

    @PostMapping
    fun createWebhook() {

    }

}

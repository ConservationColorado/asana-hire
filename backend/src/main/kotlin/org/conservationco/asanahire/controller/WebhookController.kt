package org.conservationco.asanahire.controller

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/webhook")
class WebhookController {

    @PostMapping("/create")
    fun createWebhook() {

    }

    @DeleteMapping("/delete")
    fun deleteWebhook(@RequestBody webhookId: String) {

    }

}

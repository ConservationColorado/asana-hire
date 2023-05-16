package org.conservationco.asanahire.controller

import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.config.asanaWebhookCreatePath
import org.conservationco.asanahire.config.webhookSecretHeader
import org.conservationco.asanahire.config.webhookSignatureHeader
import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.service.WebhookService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
class WebhookController(
    private val webhookService: WebhookService,
) {

    @PostMapping(asanaWebhookCreatePath)
    fun asanaWebhookEntrypoint(
        @RequestHeader(webhookSecretHeader) secret: String?,
        @RequestHeader(webhookSignatureHeader) signature: String?,
        @RequestBody body: String?
    ) = Mono.just(webhookService.processWebhookRequest(secret, signature, body))

    @PostMapping("/webhook/new")
    fun newWebhookRequest(@RequestBody job: Job): String = asanaContext { return "" }

    @DeleteMapping("/webhook/delete")
    fun deleteWebhook(@RequestBody webhookId: String) = asanaContext { }

}

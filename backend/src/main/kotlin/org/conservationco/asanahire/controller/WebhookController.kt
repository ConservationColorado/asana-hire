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

    /**
     * Webhook handshake initiation, heartbeat, and event receiving endpoint that returns the following:
     *  - `204 No Content`    if [secret], [signature], and [body] are null or empty
     *  - `204 No Content`    for valid secrets, with `X-Hook-Secret=`[secret]`
     *  - `200 OK`            if [secret], [signature], and [body] align
     *  - `400 Bad Request`   otherwise
     *
     *  @param secret the shared secret to establish
     *  @param signature the HMAC SHA256 signature of the request body, derived from the shared secret
     *  @param body the JSON `String` containing event data
     */
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

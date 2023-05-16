package org.conservationco.asanahire.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
internal class WebhookControllerTest(
    @Autowired private val client: WebTestClient,
) {

}

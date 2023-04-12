package org.conservationco.asanahire.service

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import jakarta.mail.Message.RecipientType
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.conservationco.asanahire.model.mail.DefaultEmailTemplate
import org.conservationco.asanahire.model.mail.GenericAddress
import org.conservationco.asanahire.model.mail.GenericEmailMessage
import org.conservationco.asanahire.model.mail.GenericMessageBody
import org.conservationco.asanahire.util.MessageProcessor
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.util.*

@Service
class MailService(
    private val userService: UserService,
) {

    internal fun createUpdateEmailBody(recipient: GenericAddress, jobTitle: String) =
        DefaultEmailTemplate.UPDATE.createMessageBody(recipient, jobTitle)

    internal fun createRejectionEmailBody(recipient: GenericAddress, jobTitle: String) =
        DefaultEmailTemplate.REJECTION.createMessageBody(recipient, jobTitle)

    private fun DefaultEmailTemplate.createMessageBody(recipient: GenericAddress, jobTitle: String) =
        MessageProcessor(
            mapOf(
                "@recipient.name" to recipient.formattedName,
                "@job.title" to jobTitle,
            )
        ).process(content)

    internal fun createMessage(recipient: GenericAddress, content: GenericMessageBody) =
        userService
            .getCurrentlyAuthenticatedUserEmail()
            .map {
                val sender = GenericAddress("", it)
                GenericEmailMessage(sender, recipient, content)
            }

    internal fun send(message: GenericEmailMessage): Mono<Message> {
        return if (message.isValid()) sendGmailMessage(message)
        else Mono.empty()
    }

    /**
     * Sends an email from the currently authenticated user's Gmail mailbox to the specified recipient, using the specified
     * email subject and body text.
     */
    private fun sendGmailMessage(message: GenericEmailMessage): Mono<Message> {
        val (sender, recipient, body) = message
        return userService
            .getCurrentlyAuthenticatedAuthorizedClient()
            .mapNotNull {
                val gmail = createGmailServiceWithToken(it)
                val content = createEmail(recipient.email, sender.email, body.title, body.text)
                val email = createGmailMessage(content)
                gmail
                    .users()
                    .messages()
                    .send("me", email)
                    .execute()
            }
    }

    /**
     * Creates a Gmail instance using the given access token and this application's name.
     */
    private fun createGmailServiceWithToken(client: OAuth2AuthorizedClient): Gmail {
        val credential = GoogleCredential()
            .setAccessToken(client.accessToken.tokenValue)
            .setRefreshToken(client.refreshToken?.tokenValue)
        return Gmail
            .Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("asana-hire")
            .build()
    }

    /**
     * Returns a [MimeMessage] with `text/html` and the provided parameters.
     */
    private fun createEmail(toAddress: String, fromAddress: String, subject: String, body: String): MimeMessage {
        val session = Session.getDefaultInstance(Properties(), null)
        val email = MimeMessage(session)
        email.setFrom(InternetAddress(fromAddress))
        email.addRecipient(RecipientType.TO, InternetAddress(toAddress))
        email.subject = subject
        email.setText(body)
        email.setHeader("Content-Type", "text/html; charset=utf-8")
        return email
    }

    /**
     * Returns a Gmail [Message] from a [MimeMessage].
     *
     * @param content Email to be set to raw of [Message].
     */
    private fun createGmailMessage(content: MimeMessage): Message? {
        val buffer = ByteArrayOutputStream()
        content.writeTo(buffer)
        val rawMessageBytes = buffer.toByteArray()
        val encodedEmail = Base64.getUrlEncoder().withoutPadding().encodeToString(rawMessageBytes)
        return Message().setRaw(encodedEmail)
    }

}

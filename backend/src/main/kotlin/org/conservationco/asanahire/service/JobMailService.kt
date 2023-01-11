package org.conservationco.asanahire.service

import kotlinx.html.body
import kotlinx.html.html
import kotlinx.html.unsafe
import net.axay.simplekotlinmail.delivery.send
import net.axay.simplekotlinmail.email.emailBuilder
import net.axay.simplekotlinmail.html.withHTML
import org.conservationco.asanahire.domain.mail.Address
import org.conservationco.asanahire.domain.mail.template.Template
import org.conservationco.asanahire.domain.mail.template.TemplatedJobMessage
import org.simplejavamail.api.email.Email
import org.simplejavamail.api.mailer.Mailer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class JobMailService(
    @Autowired private val mailer: Mailer
) {

    // todo allow for layouts: injection of templates within templates
    suspend fun send(message: TemplatedJobMessage) {
        val senderAddress = message.sender.address
        val recipientAddress = message.recipient.address
        if (senderAddress.isEmpty() || recipientAddress.isEmpty()) return
        val email: Email = emailBuilder {
            from(message.sender.address)
            to(message.recipient.address)
            withSubject(message.subject)
            withHTML {
                html {
                    body {
                        unsafe { // todo generate in a loop,  map @ keys with replace values and replace all
                            +readTemplate("email-opening.html")
                                .replace("@recipient.name", message.recipient.name)
                            +readTemplate(message.template.source)
                                .replace("@recipient.name", message.recipient.name)
                                .replace("@job.title", message.jobTitle)
                            +readTemplate("email-closing.html")
                                .replace("@sender.name", message.sender.name)
                        }
                    }
                }
            }
        }
        email.send(mailer)
    }

    fun makeJobTemplate(template: Template, receiver: Address, jobTitle: String): TemplatedJobMessage {
        val title: String = when (template) {
            Template.UPDATE,
            Template.REJECTION -> "Update: Your application to our $jobTitle opening"
        }
        val sender = Address("", System.getenv("email_username"))
        return TemplatedJobMessage(template, sender, receiver, title, jobTitle)
    }

    suspend fun emailReceiptOfApplication(name: String, email: String, title: String) {
        val template = makeJobTemplate(Template.UPDATE, Address(name, email), title)
        send(template)
    }

    suspend fun emailRejection(name: String, email: String, title: String) {
        val template = makeJobTemplate(Template.REJECTION, Address(name, email), title)
        send(template)
    }

    private fun readTemplate(name: String): String = File("backend/src/main/resources/templates/$name").readText()

}

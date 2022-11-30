package org.conservationco.asanahire.mail.template

import org.conservationco.asanahire.mail.Address

class TemplatedJobMessage(
    template: Template,
    sender: Address,
    recipient: Address,
    subject: String,
    val jobTitle: String
): TemplatedMessage(template, sender, recipient, subject)

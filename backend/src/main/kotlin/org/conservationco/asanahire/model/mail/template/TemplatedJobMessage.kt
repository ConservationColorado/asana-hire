package org.conservationco.asanahire.model.mail.template

import org.conservationco.asanahire.model.mail.Address

class TemplatedJobMessage(
    template: Template,
    sender: Address,
    recipient: Address,
    subject: String,
    val jobTitle: String
): TemplatedMessage(template, sender, recipient, subject)

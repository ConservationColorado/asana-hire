package org.conservationco.asanahire.domain.mail.template

import org.conservationco.asanahire.domain.mail.Address
import org.conservationco.asanahire.domain.mail.Message

open class TemplatedMessage (
    val template: Template,
    override val sender: Address,
    override val recipient: Address,
    override val subject: String
): Message

package org.conservationco.asanahire.mail.template

import org.conservationco.asanahire.mail.Address
import org.conservationco.asanahire.mail.Message

open class TemplatedMessage (
    val template: Template,
    override val sender: Address,
    override val recipient: Address,
    override val subject: String
): Message

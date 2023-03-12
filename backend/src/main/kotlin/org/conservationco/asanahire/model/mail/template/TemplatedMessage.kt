package org.conservationco.asanahire.model.mail.template

import org.conservationco.asanahire.model.mail.Address
import org.conservationco.asanahire.model.mail.Message

open class TemplatedMessage (
    val template: Template,
    override val sender: Address,
    override val recipient: Address,
    override val subject: String
): Message

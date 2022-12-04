package org.conservationco.asanahire.domain.mail

interface Message {
    val sender: Address
    val recipient: Address
    val subject: String
}

package org.conservationco.asanahire.model.mail

interface Message {
    val sender: Address
    val recipient: Address
    val subject: String
}

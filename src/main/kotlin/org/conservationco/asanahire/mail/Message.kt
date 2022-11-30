package org.conservationco.asanahire.mail

interface Message {
    val sender: Address
    val recipient: Address
    val subject: String
}

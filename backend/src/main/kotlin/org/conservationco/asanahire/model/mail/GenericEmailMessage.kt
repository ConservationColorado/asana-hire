package org.conservationco.asanahire.model.mail

data class GenericEmailMessage(
    val sender: GenericAddress,
    val recipient: GenericAddress,
    val content: GenericMessageBody,
) {
    fun isValid() = recipient.isValid() && content.isValid()
}

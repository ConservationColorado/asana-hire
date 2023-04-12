package org.conservationco.asanahire.model.mail

data class GenericMessageBody(
    val title: String,
    val text: String,
) {
    fun isValid() = title.isNotEmpty() && text.isNotEmpty()
}

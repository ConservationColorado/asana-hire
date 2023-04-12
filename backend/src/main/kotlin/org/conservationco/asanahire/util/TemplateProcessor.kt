package org.conservationco.asanahire.util

import org.conservationco.asanahire.model.mail.GenericMessageBody

data class MessageProcessor(
    private val template: Map<String, String>,
) {

    private val regex = template.keys.joinToString("|").toRegex()

    fun process(content: GenericMessageBody) =
        GenericMessageBody(
            processWithRegex(content.title),
            processWithRegex(content.text)
        )

    private fun processWithRegex(text: String) =
        text.replace(regex) { match -> template[match.value] ?: match.value }

}

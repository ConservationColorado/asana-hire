package org.conservationco.asanahire.model.mail

data class GenericAddress(
    val name: String,
    val email: String
) {
    val formattedName = name.toDisplayCase()
    fun isValid() = name.isNotEmpty() && email.isNotEmpty()
}

private fun String.toDisplayCase() =
    this.split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
package org.conservationco.asanahire.domain.mail

data class Address(
    val name: String,
    val address: String
) {

    fun name(): String = toDisplayCase(this.name)

    private fun toDisplayCase(name: String): String {
        val capitalizeAfter = " '-/"
        val sb = StringBuilder()
        var shouldCapitalizeNext = true
        for (c in name.toCharArray()) {
            if (shouldCapitalizeNext) sb.append(c.uppercaseChar()) else sb.append(c.lowercaseChar())
            shouldCapitalizeNext = capitalizeAfter.indexOf(c) >= 0
        }
        return sb.toString()
    }

}

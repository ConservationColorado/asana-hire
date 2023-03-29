package org.conservationco.asanahire.security

import java.util.*

enum class AuthProvider {
    LOCAL,
    GOOGLE,
}

fun valueOfIgnoreCase(provider: String) =
    AuthProvider.valueOf(provider.uppercase(Locale.getDefault()))

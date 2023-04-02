package org.conservationco.asanahire.model.user

import java.util.*

enum class AuthProvider {
    LOCAL,
    GOOGLE,
}

fun valueOfIgnoreCase(provider: String) =
    AuthProvider.valueOf(provider.uppercase(Locale.getDefault()))

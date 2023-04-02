package org.conservationco.asanahire.model.mail

enum class Protocol(
    val host: String,
    val port: Int,
) {
    IMAP( "imap.gmail.com", 993),
    POP3( "pop.gmail.com", 995),
    SMTP( "smtp.gmail.com", 587)
}

package org.conservationco.asanahire.model.mail

import java.io.FileNotFoundException

enum class DefaultEmailTemplate(val content: GenericMessageBody) {
    REJECTION(
        GenericMessageBody(
            "Update: Your application to our @job.title opening",
            readTemplate("applicant-rejection.html"),
        )
    ),
    UPDATE(
        GenericMessageBody(
            "Update: Your application to our @job.title opening",
            readTemplate("applicant-update.html"),
        )
    ),
}

private fun readTemplate(name: String): String =
    DefaultEmailTemplate::class.java.classLoader.getResourceAsStream("templates/$name")
        ?.let { stream -> stream.bufferedReader().use { it.readText() }  }
        ?: throw FileNotFoundException("Couldn't read template at classpath://templates/$name")

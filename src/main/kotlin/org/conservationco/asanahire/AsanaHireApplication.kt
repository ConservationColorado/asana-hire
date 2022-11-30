package org.conservationco.asanahire

import com.asana.models.Project
import com.asana.models.Workspace
import net.axay.simplekotlinmail.delivery.mailerBuilder
import org.conservationco.asanahire.domain.JobSource
import org.conservationco.asanahire.mail.Protocol
import org.simplejavamail.api.mailer.Mailer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class AsanaHireApplication {
    @Bean
    fun jobSource() = JobSource().apply {
        project = Project().apply { gid = env("project_job_source_gid") }
    }

    @Bean
    fun workspace() = Workspace().apply { gid = env("workspace_gid") }

    @Bean
    fun mailer(): Mailer {
        val protocol = Protocol.SMTP
        return mailerBuilder(
            host = protocol.host,
            port = protocol.port,
            username = env("email_username"),
            password = env("email_password"),
        )
    }

    private fun env(variable: String) = System.getenv(variable)
}

fun main(args: Array<String>) {
    runApplication<AsanaHireApplication>(*args)
}




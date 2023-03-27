package org.conservationco.asanahire.config

import com.asana.models.Portfolio
import com.asana.models.Workspace
import net.axay.simplekotlinmail.delivery.mailerBuilder
import org.conservationco.asanahire.model.asana.JobSource
import org.conservationco.asanahire.model.mail.Protocol
import org.simplejavamail.api.mailer.Mailer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfiguration {

    @Bean
    fun jobSource() = JobSource().apply {
        applicationPortfolio = Portfolio().apply { gid = env("application_portfolio_gid") }
        interviewPortfolio = Portfolio().apply { gid = env("interview_portfolio_gid") }
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

}

private fun env(variable: String) = System.getenv(variable)

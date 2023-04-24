package org.conservationco.asanahire.config

import com.asana.models.Portfolio
import com.asana.models.Workspace
import org.conservationco.asanahire.model.asana.JobSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainConfiguration {

    @Bean
    fun jobSource() = JobSource().apply {
        applicationPortfolio = Portfolio().apply { gid = env("ASANA_APPLICATION_PORTFOLIO_GID") }
        interviewPortfolio = Portfolio().apply { gid = env("ASANA_INTERVIEW_PORTFOLIO_GID") }
    }

    @Bean
    fun workspace() = Workspace().apply { gid = env("ASANA_WORKSPACE_GID") }

}

private fun env(variable: String) = System.getenv(variable)

package org.conservationco.asanahire.config

import com.asana.models.Portfolio
import com.asana.models.Workspace
import com.google.api.client.util.DateTime
import com.google.gson.*
import org.conservationco.asanahire.model.asana.JobSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.lang.reflect.Type

@Configuration
class DomainConfiguration {

    @Bean
    fun jobSource() = JobSource().apply {
        applicationPortfolio = Portfolio().apply { gid = env("ASANA_APPLICATION_PORTFOLIO_GID") }
        interviewPortfolio = Portfolio().apply { gid = env("ASANA_INTERVIEW_PORTFOLIO_GID") }
    }

    @Bean
    fun workspace() = Workspace().apply { gid = env("ASANA_WORKSPACE_GID") }

    @Bean
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(DateTime::class.java, DateTimeDeserializer())
        .create()

    private class DateTimeDeserializer : JsonDeserializer<DateTime> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DateTime {
            val dateTimeString = json.asString
            return DateTime(dateTimeString)
        }
    }

}

private fun env(variable: String) = System.getenv(variable)

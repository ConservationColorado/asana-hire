package org.conservationco.asanahire.service

import com.asana.models.Project
import org.conservationco.asana.asanaContext
import org.conservationco.asana.serialization.AsanaSerializable
import org.conservationco.asanahire.domain.Applicant
import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.repository.ApplicantRepository
import org.conservationco.asanahire.repository.ManagerApplicantRepository
import org.conservationco.asanahire.repository.OriginalApplicantRepository
import org.springframework.stereotype.Service

@Service
internal class ApplicantService(
    private val originalRepository: OriginalApplicantRepository,
    private val managerRepository: ManagerApplicantRepository,
) {

    fun sync(job: Job) {
        TODO()
    }

    private inline fun <reified T> sync(job: Job, repository: ApplicantRepository<T>)
    where T: Applicant,
          T: AsanaSerializable<T> = asanaContext {
        val source = project(job.originalSourceId)
        val sourceCount = source.getTaskCount().toLong()
        val repoCount = repository.count()

        if (sourceCount != 0L && sourceCount != repoCount) {
            if (repoCount == 0L) saveAllToRepo(source, repository)
            else {
                val newTasks= source
                    .getNewTasks(true)
                    .convertToListOf(T::class, source)
                repository.saveAll(newTasks)
            }
        }
    }

    private inline fun <reified T> saveAllToRepo(
        source: Project,
        repository: ApplicantRepository<T>
    ): List<Applicant>
    where T: Applicant,
          T: AsanaSerializable<T> = asanaContext {
        return source
            .convertTasksToListOf(T::class, true)
            .also { repository.saveAll(it) }
    }

}

package org.conservationco.asanahire.service

import com.asana.models.Task
import com.asana.models.Workspace
import kotlinx.coroutines.*
import org.conservationco.asana.asanaContext
import org.conservationco.asana.util.AsanaTable
import org.conservationco.asanahire.domain.*
import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.asanahire.repository.getJob
import org.conservationco.asanahire.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ApplicantService(
    private val jobRepository: JobRepository,
    @Autowired private val mailer: JobMailService,
    @Autowired private val workspace: Workspace,
) {

    private val applicantScope = CoroutineScope(SupervisorJob())

    fun getNewApplicants(jobId: Long, time: LocalDateTime): Deferred<List<ApplicantEvent>> = asanaContext {
        return applicantScope.async {
            workspace
                .search(
                    "created_at.after" to time.toString(),
                    "projects.any" to jobId.toString()
                )
                .map(Task::toApplicantEvent)
        }
    }

    fun getAllNeedingRejection(jobId: Long): Deferred<List<RejectableApplicant>> =
        jobRepository.getJob(jobId) { job ->
            return@getJob applicantScope.async { rejectableApplicants(job) }
        }

    fun rejectApplicant(applicant: RejectableApplicant) {
        applicantScope.launch {
            jobRepository.getJob(applicant.jobId) { job ->
                launch { updateRejectionStatusFor(job, applicant) }
                launch { sendRejectionEmail(applicant, job) }
            }
        }
    }

    private suspend fun sendRejectionEmail(applicant: RejectableApplicant, job: Job) =
        mailer.emailRejection(applicant.preferredName, applicant.email, job.title)

    private fun updateRejectionStatusFor(
        job: Job,
        applicant: RejectableApplicant
    ) {
        asanaContext {
            val project = project(job.applicationProjectId)
            task(applicant.id)
                .get()
                .convertToOriginalApplicant()
                .updateRejectionStatus(project)
        }
    }

    private fun rejectableApplicants(job: Job): List<RejectableApplicant> {
        val originalApplicants = AsanaTable.tableFor<OriginalApplicant>(
            job.applicationProjectId,
            deserializingFn = applicantDeserializingFn()
        )
        val interviewApplicants = AsanaTable.tableFor<InterviewApplicant>(
            job.interviewProjectId,
            deserializingFn = applicantDeserializingFn()
        )
        if (originalApplicants.size() == 0 || interviewApplicants.size() == 0) return emptyList()

        val applicantsThatNeedRejection = interviewApplicants
            .getAll()
            .filter { it.needsRejection() }
            .associateBy { it.email }
        if (applicantsThatNeedRejection.isEmpty()) return emptyList()

        return originalApplicants
            .getAll()
            .asSequence()
            .filter { !it.hasBeenRejected() }
            .filter { applicantsThatNeedRejection.containsKey(it.email) }
            .map {
                RejectableApplicant(
                    it.id,
                    job.id,
                    it.name,
                    it.preferredName,
                    it.email
                )
            }
            .toList()
    }

}

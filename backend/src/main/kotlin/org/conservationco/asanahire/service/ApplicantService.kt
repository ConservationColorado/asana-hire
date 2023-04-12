package org.conservationco.asanahire.service

import com.asana.models.Task
import com.asana.models.Workspace
import org.conservationco.asana.asanaContext
import org.conservationco.asana.util.AsanaTable
import org.conservationco.asanahire.model.applicant.*
import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.model.mail.GenericAddress
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.asanahire.util.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class ApplicantService(
    private val jobRepository: JobRepository,
    private val mailService: MailService,
    private val workspace: Workspace,
) {

    internal fun getNewApplicants(jobId: Long, time: LocalDateTime): Mono<List<ApplicantEvent>> =
        asanaContext {
            Mono.fromCallable {
                workspace
                    .search(
                        "created_at.after" to time.toString(),
                        "projects.any" to jobId.toString()
                    )
                    .map(Task::toApplicantEvent)
            }
        }

    internal fun getAllNeedingRejection(jobId: Long): Mono<List<RejectableApplicant>> =
        jobRepository
            .findById(jobId)
            .map { rejectableApplicants(it) }

    internal fun rejectApplicant(applicant: RejectableApplicant) =
        jobRepository
            .findById(applicant.jobId)
            .flatMap {
                updateRejectionStatusFor(it, applicant)
                sendRejectionEmail(applicant, it.title)
            }

    private fun sendRejectionEmail(applicant: RejectableApplicant, jobTitle: String): Mono<Void> {
        val recipient = GenericAddress(applicant.name, applicant.email)
        val message = mailService.createRejectionEmailBody(recipient, jobTitle)
        return mailService
            .createMessage(recipient, message)
            .flatMap { mailService.send(it) }
            .then()
    }

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

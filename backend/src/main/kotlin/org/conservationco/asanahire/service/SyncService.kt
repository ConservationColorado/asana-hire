package org.conservationco.asanahire.service

import com.asana.models.Project
import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.exception.JobNotFoundException
import org.conservationco.asanahire.exception.SyncNotFoundException
import org.conservationco.asanahire.model.applicant.InterviewApplicant
import org.conservationco.asanahire.model.applicant.OriginalApplicant
import org.conservationco.asanahire.model.asana.ApplicantSyncPair
import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.model.mail.GenericAddress
import org.conservationco.asanahire.model.sync.RequestState.*
import org.conservationco.asanahire.model.sync.SyncEvent
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.asanahire.repository.SyncEventRepository
import org.conservationco.asanahire.util.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Service
class SyncService(
    private val jobRepository: JobRepository,
    private val syncEventRepository: SyncEventRepository,
    private val mailService: MailService,
) {

    private val jobLocks = ConcurrentHashMap<Long, Any>()

    internal fun getAllSyncs() =
        syncEventRepository
            .findAll()
            .collectList()

    internal fun getSync(syncId: Long) =
        syncEventRepository
            .findById(syncId)
            .switchIfEmpty(Mono.error(SyncNotFoundException(syncId)))

    internal fun startSync(jobId: Long) =
        Mono.defer {
            acquireLock(jobId)
                .then(syncEventRepository.findById(jobId))
                .switchIfEmpty(launchNewSync(jobId))
                .flatMap { handleExistingRequest(it) }
                .doFinally { releaseLock(jobId) }
        }

    private fun acquireLock(jobId: Long): Mono<Void> {
        val lock = jobLocks.computeIfAbsent(jobId) { Object() }
        return Mono.create { sink ->
            synchronized(lock) {
                sink.success()
            }
        }
    }

    private fun releaseLock(jobId: Long) {
        jobLocks.remove(jobId)
    }

    private fun handleExistingRequest(request: SyncEvent) =
        when (request.status) {
            NOT_STARTED, IN_PROGRESS -> Mono.just(request)
            COMPLETE -> launchNewSync(request.jobId)
        }

    private fun launchNewSync(jobId: Long) =
        jobRepository
            .findById(jobId)
            .switchIfEmpty(Mono.error(JobNotFoundException(jobId)))
            .flatMap { checkIfSyncNeeded(it) }

    private fun checkIfSyncNeeded(job: Job): Mono<SyncEvent> {
        val snapshot = SyncedProjectsSnapshot(
            job.title,
            Project().apply { gid = job.applicationProjectId },
            Project().apply { gid = job.interviewProjectId }
        )
        return if (snapshot.needsSyncing()) startProjectSync(job, snapshot)
        else completedEvent(job)
    }

    private fun startProjectSync(job: Job, snapshot: SyncedProjectsSnapshot) =
        syncEventRepository
            .save(inProgressEvent(job))
            .flatMap { startSync(job, snapshot).then(Mono.just(it)) }

    private fun startSync(job: Job, snapshot: SyncedProjectsSnapshot): Flux<Void> {
        val (jobTitle, source, destination) = snapshot
        val applicants = getNewApplicants(job, snapshot)
        return Flux.fromIterable(applicants)
            .flatMap { syncSingleApplicant(it, destination, jobTitle, source) }
    }

    /**
     * For each new applicant:
     *
     * * Add that applicant as a task to the manager project
     * * Emails a receipt of application confirmation message to the applicant
     * * Update the original task's receipt stage
     *
     * These are completed synchronously with guaranteed completion order.
     */
    private fun syncSingleApplicant(
        applicantPair: ApplicantSyncPair,
        destination: Project,
        jobTitle: String,
        source: Project,
    ): Mono<Void> {
        val (originalApplicant, interviewApplicant) = applicantPair
        addToInterviewProject(interviewApplicant, destination)
        originalApplicant.updateReceiptOfApplication(source)
        return sendReceiptEmail(originalApplicant, jobTitle)
    }

    private fun sendReceiptEmail(applicant: OriginalApplicant, jobTitle: String): Mono<Void> {
        val recipient = GenericAddress(applicant.preferredName, applicant.email)
        val message = mailService.createUpdateEmailBody(recipient, jobTitle)
        return mailService
            .createMessage(recipient, message)
            .flatMap { mailService.send(it) }
            .then()
    }

    private fun addToInterviewProject(
        interviewApplicant: InterviewApplicant,
        destination: Project
    ) = asanaContext {
        val task = interviewApplicant.convertToTask(destination, applicantSerializingFn())
        destination.createTask(task)
    }

    /**
     * Function has multiple fallback measures: Asana event stream is somewhat unreliable & always empty if no events
     * polled in last 24hrs.
     */
    internal fun getNewApplicants(
        job: Job,
        snapshot: SyncedProjectsSnapshot
    ): List<ApplicantSyncPair> =
        asanaContext {
            val (_, source, _) = snapshot
            val newTasksFromEventStream = source.getNewTasks(true)
            val applicantPairsForSync =
                newTasksFromEventStream
                    .ifEmpty { source.getTasks(true) }
                    .asSequence()
                    .map { it to it.convertToOriginalApplicant() }
                    .filter { it.second.needsSyncing() }
                    .map {
                        ApplicantSyncPair(
                            it.second,
                            it.first.convertToManagerApplicant()
                        )
                    }.toList()
            return applicantPairsForSync
        }

    private fun completedEvent(job: Job): Mono<SyncEvent> {
        val event = SyncEvent(
            jobId = job.id,
            status = COMPLETE
        )
        return syncEventRepository.save(event)
    }

    private fun inProgressEvent(job: Job) =
        SyncEvent(
            jobId = job.id,
            status = IN_PROGRESS
        )

}

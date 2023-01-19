package org.conservationco.asanahire.service

import com.asana.models.Project
import com.asana.models.Task
import com.asana.models.Workspace
import kotlinx.coroutines.*
import org.conservationco.asana.asanaContext
import org.conservationco.asana.util.AsanaTable
import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.domain.ManagerApplicant
import org.conservationco.asanahire.domain.OriginalApplicant
import org.conservationco.asanahire.domain.RejectableApplicant
import org.conservationco.asanahire.domain.asana.ApplicantSyncPayload
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.asanahire.repository.getJob
import org.conservationco.asanahire.requests.JobSyncRequest
import org.conservationco.asanahire.requests.RequestState
import org.conservationco.asanahire.util.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ApplicantService(
    private val jobRepository: JobRepository,
    @Autowired private val workspace: Workspace,
    @Autowired private val mailer: JobMailService,
) {

    private val applicantScope = CoroutineScope(SupervisorJob())

    private var jobIdsToLastCompletedSync: MutableMap<String, LocalDateTime> = HashMap()
    private val ongoingSyncs: MutableMap<Long, JobSyncRequest> = HashMap()

    fun getAllNeedingRejection(jobId: Long): Any {
        var applicants = emptyList<RejectableApplicant>()
        jobRepository.getJob(jobId) { job -> applicants = rejectableApplicants(job) }
        return applicants
    }

    fun rejectApplicant(jobId: Long, applicant: RejectableApplicant) {
        applicantScope.launch {
            jobRepository.getJob(jobId) { job ->
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
            task(applicant.originalGid)
                .get()
                .convertToOriginalApplicant()
                .updateRejectionStatus(project)
        }
    }

    fun trySync(jobId: Long): JobSyncRequest? =
        when (val request = ongoingSyncs[jobId]) {
            null -> launchNewSync(jobId)
            else -> handleExistingRequest(jobId, request)
        }

    private fun handleExistingRequest(jobId: Long, request: JobSyncRequest): JobSyncRequest? {
        return if (request.isInProgress()) request
        else ongoingSyncs.remove(jobId)
    }

    private fun launchNewSync(jobId: Long): JobSyncRequest {
        val newRequest = JobSyncRequest(jobId)
        ongoingSyncs[jobId] = newRequest
        applicantScope.launch {
            withContext(Dispatchers.IO) {
                jobRepository.getJob(jobId) { job ->
                    launch { checkIfSyncNeeded(job) }
                }
            }
        }
        return newRequest
    }

    private suspend fun checkIfSyncNeeded(job: Job) = coroutineScope {
        val snapshot = SyncedProjectsSnapshot(
            job.title,
            Project().apply { gid = job.applicationProjectId },
            Project().apply { gid = job.interviewProjectId }
        )
        if (snapshot.needsSyncing()) startProjectSync(snapshot)
        ongoingSyncs[job.id] = JobSyncRequest(job.id, RequestState.COMPLETE)
    }

    private suspend fun startProjectSync(snapshot: SyncedProjectsSnapshot) = coroutineScope {
        val (jobTitle, source, destination) = snapshot
        val applicants = getNewApplicants(snapshot)
        for (payload in applicants) {
            syncSingleApplicant(payload, destination, jobTitle, source)
        }
        jobIdsToLastCompletedSync[source.gid] = snapshot.time
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
    private suspend fun syncSingleApplicant(
        payload: ApplicantSyncPayload,
        destination: Project,
        jobTitle: String,
        source: Project
    ) {
        val (originalApplicant, interviewApplicant) = payload
        addToInterviewProject(interviewApplicant, destination)
        sendReceiptEmail(originalApplicant, jobTitle)
        originalApplicant.updateReceiptOfApplication(source)
    }

    private suspend fun sendReceiptEmail(
        originalApplicant: OriginalApplicant,
        jobTitle: String
    ) {
        mailer.emailReceiptOfApplication(
            originalApplicant.preferredName,
            originalApplicant.email,
            jobTitle
        )
    }

    private fun addToInterviewProject(
        interviewApplicant: ManagerApplicant,
        destination: Project
    ) {
        asanaContext {
            val task = interviewApplicant.convertToTask(destination, applicantSerializingFn())
            destination.createTask(task)
        }
    }

    /**
     * Function has multiple fallback measures: Asana event stream is somewhat unreliable & always empty if no events 
     * polled in last 24hrs.
     */
    private fun getNewApplicants(snapshot: SyncedProjectsSnapshot): List<ApplicantSyncPayload> = asanaContext {
        val (_, source, _) = snapshot
        val newTasksFromEventStream = source.getNewTasks(true)
        // Multiple fallback measures since Asana event stream always empty if no events polled in last 24hrs
        val applicantsToSync =
            newTasksFromEventStream
                .ifEmpty { source.getNewTasksBySearchOrByForce() }
                .asSequence()
                .map { it to it.convertToOriginalApplicant() }
                .filter { it.second.needsSyncing() }
                .map { 
                    ApplicantSyncPayload(
                        it.second, 
                        it.first.convertToManagerApplicant()
                    )
                }.toList()
        return applicantsToSync
    }

    private fun Project.getNewTasksBySearchOrByForce(): List<Task> = asanaContext {
        val lastSync = jobIdsToLastCompletedSync[gid]
        return if (lastSync == null) getTasks(true)
        else this@ApplicantService.workspace.search(
            "created_at.after" to lastSync,
            "projects.any" to gid
        )
    }

    private fun rejectableApplicants(job: Job): List<RejectableApplicant> {
        val originalApplicants = AsanaTable.tableFor<OriginalApplicant>(
            job.applicationProjectId,
            deserializingFn = applicantDeserializingFn()
        )
        val managerApplicants = AsanaTable.tableFor<ManagerApplicant>(
            job.interviewProjectId,
            deserializingFn = applicantDeserializingFn()
        )
        if (originalApplicants.size() == 0 || managerApplicants.size() == 0) return emptyList()

        val applicantsThatNeedRejection = managerApplicants
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
                    it.name,
                    it.preferredName,
                    it.email
                )
            }
            .toList()
    }

}

package org.conservationco.asanahire.service

import com.asana.models.Project
import com.asana.models.Task
import com.asana.models.Workspace
import kotlinx.coroutines.*
import org.conservationco.asana.asanaContext
import org.conservationco.asana.util.AsanaTable
import org.conservationco.asanahire.domain.ApplicantPayload
import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.domain.ManagerApplicant
import org.conservationco.asanahire.domain.OriginalApplicant
import org.conservationco.asanahire.domain.mail.Address
import org.conservationco.asanahire.domain.mail.template.Template
import org.conservationco.asanahire.exception.NoSuchJobException
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.asanahire.requests.JobSyncRequest
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

    private var jobIdsToLastCompletedSync: MutableMap<String, LocalDateTime> = HashMap()
    private val ongoingSyncs: MutableSet<JobSyncRequest> = HashSet()

    private val applicantScope = CoroutineScope(SupervisorJob())

    suspend fun getAllNeedingRejection(jobId: String): List<OriginalApplicant> {
        var applicants = emptyList<OriginalApplicant>()
        withContext(Dispatchers.IO) {
            jobRepository
                .findById(jobId)
                .ifPresentOrElse(
                    { applicants = rejectableApplicants(it) },
                    { throw NoSuchElementException() }
                )
        }
        return applicants
    }

    fun trySync(jobId: String): JobSyncRequest {
        val request = JobSyncRequest(jobId)
        if (ongoingSyncs.contains(request)) return request
        applicantScope.launch {
            withContext(Dispatchers.IO) {
                jobRepository
                    .findById(jobId)
                    .ifPresentOrElse(
                        { job -> launch { checkIfSyncNeeded(job, request) } },
                        { throw NoSuchJobException() }
                    )
            }
        }
        return request
    }

    private suspend fun checkIfSyncNeeded(job: Job, jobSyncRequest: JobSyncRequest) = coroutineScope {
        val snapshot = SyncedProjectsSnapshot(
            job.title,
            Project().apply { gid = job.originalSourceId },
            Project().apply { gid = job.managerSourceId }
        )
        if (snapshot.needsSyncing()) doApplicantSync(snapshot, jobSyncRequest)
        ongoingSyncs.remove(jobSyncRequest)
    }

    /**
     * For each new applicant:
     *
     * * Add that applicant as a task to the manager project
     * * Emails a receipt of application confirmation message to the applicant
     * * Update the original task's receipt stage
     *
     * These are completed asynchronously with no guarantees of completion order.
     */
    private suspend fun doApplicantSync(
        snapshot: SyncedProjectsSnapshot,
        jobSyncRequest: JobSyncRequest
    ) = coroutineScope {
        val (jobTitle, source, destination) = snapshot
        val applicants = getNewApplicants(snapshot)
        for (payload in applicants) {
            val (originalApplicant, _, _, managerTask) = payload
            launch { destination.createTask(managerTask) }
            launch { updateReceiptOfApplication(source, originalApplicant) }
            launch { emailReceiptOfApplication(originalApplicant.preferredName, originalApplicant.email, jobTitle) }
        }
        jobIdsToLastCompletedSync[source.gid] = snapshot.time
    }

    private fun updateReceiptOfApplication(source: Project, originalApplicant: OriginalApplicant) = asanaContext {
        originalApplicant
            .copy()
            .apply { receiptStage = "✅" }
            .convertToTask(source, applicantSerializingFn())
            .update()
    }

    private fun getNewApplicants(snapshot: SyncedProjectsSnapshot): List<ApplicantPayload> = asanaContext {
        val (_, source, destination) = snapshot
        val newTasksFromEventStream = source.getNewTasks(true)
        val applicantsToSync =
            // Multiple fallback measures:
            // Asana event stream is unreliable & always empty if no events polled in last 24hrs
            newTasksFromEventStream
                .ifEmpty { source.getNewTasksBySearchOrByForce() }
                .associateWith { it.convertToOriginalApplicant()}
                .filter { it.value.needsSyncing() }
        return prepareTasksForSync(destination, applicantsToSync)
    }

    private fun prepareTasksForSync(
        destination: Project,
        originalTasksToApplicants: Map<Task, OriginalApplicant>
    ) = asanaContext {
        originalTasksToApplicants.map {
            val (originalTask, originalApplicant) = it
            val managerApplicant = originalTask.convertToManagerApplicant()
            val managerTask = managerApplicant.convertToTask(destination, applicantSerializingFn())
            ApplicantPayload(
                originalApplicant,
                managerApplicant,
                originalTask,
                managerTask
            )
        }
    }

    private suspend fun Project.createTask(taskToCreate: Task) = asanaContext {
        createTask(taskToCreate)
    }

    private suspend fun emailReceiptOfApplication(name: String, email: String, title: String) {
        val template = mailer.makeJobTemplate(Template.UPDATE, Address(name, email), title)
        mailer.send(template)
    }

    private fun Project.getNewTasksBySearchOrByForce(): List<Task> = asanaContext {
        val lastSync = jobIdsToLastCompletedSync[gid]
        return if (lastSync == null) getTasks(true)
        else this@ApplicantService.workspace.search(
            "created_at.after" to lastSync,
            "projects.any" to gid
        )
    }

    private fun rejectableApplicants(job: Job): List<OriginalApplicant> {
        val originalApplicants = AsanaTable.tableFor<OriginalApplicant>(job.originalSourceId)
        val managerApplicants = AsanaTable.tableFor<ManagerApplicant>(job.managerSourceId)
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
            .filter { applicantsThatNeedRejection[it.email] != null }
            .toList()
    }

}

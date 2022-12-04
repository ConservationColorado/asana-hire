package org.conservationco.asanahire.service

import com.asana.models.Project
import com.asana.models.Task
import com.asana.models.Workspace
import kotlinx.coroutines.*
import org.conservationco.asana.asanaContext
import org.conservationco.asana.util.AsanaTable
import org.conservationco.asanahire.domain.*
import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.repository.JobRepository
import org.conservationco.asanahire.repository.getJob
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

    private val applicantScope = CoroutineScope(SupervisorJob())

    private var jobIdsToLastCompletedSync: MutableMap<String, LocalDateTime> = HashMap()
    private val ongoingSyncs: MutableSet<JobSyncRequest> = HashSet()
    private val ongoingRejections: MutableMap<String, List<OriginalApplicant>> = HashMap()

    fun getAllNeedingRejection(jobId: String): Any {
        var applicants = emptyList<RejectableApplicant>()
        jobRepository.getJob(jobId) { job -> applicants = rejectableApplicants(job) }
        return applicants
    }

    fun rejectApplicant(jobId: String, applicant: RejectableApplicant) {
        applicantScope.launch {
            jobRepository.getJob(jobId) { job ->
                launch {
                    asanaContext {
                        val project = project(job.originalSourceId)
                        task(applicant.originalGid)
                            .get()
                            .convertToOriginalApplicant()
                            .updateRejectionStatus(project)
                    }
                }
                launch {
                    mailer.emailRejection(applicant.name, applicant.email, job.title)
                }
            }
        }
    }

    fun trySync(jobId: String): JobSyncRequest {
        val request = JobSyncRequest(jobId)
        if (ongoingSyncs.contains(request)) return request
        applicantScope.launch {
            withContext(Dispatchers.IO) {
                jobRepository.getJob(jobId) { job ->
                    launch { checkIfSyncNeeded(job, request) }
                }
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
        if (snapshot.needsSyncing()) doApplicantSync(snapshot)
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
    private suspend fun doApplicantSync(snapshot: SyncedProjectsSnapshot) = coroutineScope {
        val (jobTitle, source, destination) = snapshot
        val applicants = getNewApplicants(snapshot)
        for (payload in applicants) {
            val (originalApplicant, _, _, managerTask) = payload
            launch { destination.createTask(managerTask) }
            launch { originalApplicant.updateReceiptOfApplication(source) }
            launch { mailer.emailReceiptOfApplication(
                originalApplicant.preferredName,
                originalApplicant.email,
                jobTitle)
            }
        }
        jobIdsToLastCompletedSync[source.gid] = snapshot.time
    }

    private fun Project.createTask(taskToCreate: Task) = asanaContext {
        createTask(taskToCreate)
    }

    private fun OriginalApplicant.updateReceiptOfApplication(source: Project) =
        copyAndUpdate(source) { receiptStage = "✅" }

    private fun OriginalApplicant.updateRejectionStatus(source: Project) =
        copyAndUpdate(source) { rejectionStage = "✅" }

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
            job.originalSourceId,
            deserializingFn = applicantDeserializingFn()
        )
        val managerApplicants = AsanaTable.tableFor<ManagerApplicant>(
            job.managerSourceId,
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
                    applicantsThatNeedRejection[it.email]?.id.orEmpty(),
                    it.name,
                    it.preferredName,
                    it.email
                )
            }
            .toList()
    }

}

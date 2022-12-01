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
import org.conservationco.asanahire.mail.Address
import org.conservationco.asanahire.mail.template.Template
import org.conservationco.asanahire.repository.JobRepository
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

    private var lastSync = LocalDateTime.MIN

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

    suspend fun trySync(jobId: String) {
        withContext(Dispatchers.IO) {
            jobRepository
                .findById(jobId)
                .ifPresentOrElse(
                    { job -> launch { checkIfSyncNeeded(job) } },
                    { throw NoSuchElementException() }
                )
        }
    }

    private suspend fun checkIfSyncNeeded(job: Job) = coroutineScope {
        val snapshot = SyncedProjectsSnapshot(
            job.title,
            Project().apply { gid = job.originalSourceId },
            Project().apply { gid = job.managerSourceId }
        )
        if (snapshot.needsSyncing()) doApplicantSync(snapshot)
    }

    private suspend fun doApplicantSync(snapshot: SyncedProjectsSnapshot) = coroutineScope {
        val (title, source, destination) = snapshot

        val newTasks = getNewTasks(snapshot)
        for (newTask in newTasks) {
            val (email, taskToCreate) = newTask
            // Add that task to the manager project
            launch { destination.createTask(taskToCreate) }
            // Send a receipt of application confirmation message to the applicant
            launch { emailReceiptOfApplication(taskToCreate.name, email, title) }
            val updatedOriginal = OriginalApplicant(
                receiptStage = "✅",
                id = taskToCreate.gid, // Converted task still has the original task's gid, so reuse it here
                managerAlias = deferredCreatedTask.await().gid,
            )
        }
        lastSync = snapshot.time
    }

    private suspend fun Project.createTask(taskToCreate: Task) = asanaContext {
        createTask(taskToCreate)
    }

    private suspend fun emailReceiptOfApplication(name: String, email: String, title: String) {
        val template = mailer.makeJobTemplate(Template.UPDATE, Address(name, email), title)
        mailer.send(template)
    }

    private fun getNewTasks(snapshot: SyncedProjectsSnapshot): List<Pair<String, Task>> = asanaContext {
        val (_, source, destination) = snapshot
        val newApplicants = source
            .getNewTasks(true)
            .convertToListOf(OriginalApplicant::class, source, applicantDeserializingFn())
        val applicantsToAdd =
            newApplicants.ifEmpty {
                source.forceGetAllUnsyncedApplicants()
            }
        return destination.prepareAsManagerTasks(applicantsToAdd)
    }

    private fun Project.prepareAsManagerTasks(
        applicantsToAdd: List<OriginalApplicant>
    ) = asanaContext {
        applicantsToAdd
            .asSequence()
            .filter(OriginalApplicant::needsSyncing)
            .map { it.toManagerApplicant() }
            .map { it.email to it.convertToTask(this@prepareAsManagerTasks, applicantSerializingFn()) }
            .toList()
    }

    private fun Project.forceGetAllUnsyncedApplicants(): List<OriginalApplicant> = asanaContext {
        val tasks =
            if (lastSync == LocalDateTime.MIN)
                getTasks(true)
            else
                this@ApplicantService.workspace.search("?created_at.after=$lastSync", gid)
        return tasks.convertToListOf(OriginalApplicant::class, this@forceGetAllUnsyncedApplicants, applicantDeserializingFn())
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

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

    private suspend fun doApplicantSync(snapshot: SyncedProjectsSnapshot) {
        val (title, source, destination) = snapshot

        withContext(Dispatchers.IO) {
            val newTasks = getNewTasks(snapshot)
            for (newTask in newTasks) {
                // Add that task to the manager project
                val createdTask = async { asanaContext { destination.createTask(newTask) } }

                // Send a receipt of application confirmation message to the applicant
                launch {
                    val template = mailer.makeJobTemplate(
                        Template.UPDATE,
                        Address(newTask.name, newTask.customFields.find { it.name == "Email" }?.textValue!!),
                        title
                    )
                    mailer.send(template)
                }

                // Add the manager task's id to the original task as a fallback
                val updatedOriginal = OriginalApplicant(
                    receiptStage = "✅",
                    id = newTask.gid,
                )
                launch {
                    asanaContext {
                        updatedOriginal
                            .convertToTask(source, applicantSerializingFn())
                            .update()
                    }
                }
            }
        }
        lastSync = snapshot.time
    }

    private fun getNewTasks(snapshot: SyncedProjectsSnapshot): List<Task> = asanaContext {
        val (_, source, destination) = snapshot
        val newApplicants = source
            .getNewTasks(true)
            .convertToListOf(OriginalApplicant::class, source, applicantDeserializingFn())
        val applicantsToAdd =
            newApplicants.ifEmpty {
                source.getAllUnsyncedApplicants()
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
            .map { it.convertToTask(this@prepareAsManagerTasks, applicantSerializingFn()) }
            .toList()
    }

    private fun Project.getAllUnsyncedApplicants(): List<OriginalApplicant> = asanaContext {
        val tasks =
            if (lastSync == LocalDateTime.MIN)
                getTasks(true)
            else
                this@ApplicantService.workspace.search("?created_at.after=$lastSync", gid)
        return tasks.convertToListOf(OriginalApplicant::class, this@getAllUnsyncedApplicants, applicantDeserializingFn())
    }

    fun getAllNeedingRejection(jobId: String): List<OriginalApplicant> {
        var applicants = emptyList<OriginalApplicant>()
        jobRepository
            .findById(jobId)
            .ifPresentOrElse(
                { applicants = rejectableApplicants(it) },
                { throw NoSuchElementException() }
            )
        return applicants
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

package org.conservationco.asanahire.service

import com.asana.models.Project
import org.conservationco.asana.asanaContext
import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.domain.OriginalApplicant
import org.conservationco.asanahire.util.SyncedProjectsSnapshot
import org.conservationco.asanahire.util.toManagerApplicant
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
internal class ApplicantService {

    private var lastSync = LocalDateTime.MIN

    fun sync(job: Job) = asanaContext {
        val snapshot = SyncedProjectsSnapshot(
            project(job.originalSourceId),
            project(job.managerSourceId)
        )
        if (snapshot.needsSyncing()) {
            checkAndAddNewApplicants(snapshot)
        }
    }

    private fun checkAndAddNewApplicants(snapshot: SyncedProjectsSnapshot) = asanaContext {
        val (source, destination) = snapshot
        val newTasks = source.getNewTasks(true)
        val applicantsToAdd=
            if (newTasks.isNotEmpty()) {
                newTasks
                    .convertToListOf(OriginalApplicant::class, source)
            } else {
                source
                    .convertTasksToListOf(OriginalApplicant::class, true)
                    .filter { it.managerAlias.isEmpty() }
            }
        addTasks(applicantsToAdd, destination)
        lastSync = snapshot.time
    }

    private fun addTasks(
        newTasks: List<OriginalApplicant>,
        destination: Project
    ) = asanaContext {
        newTasks
            .map { it.toManagerApplicant() }
            .map { it.convertToTask(destination) }
            .forEach { destination.createTask(it) }
    }

}

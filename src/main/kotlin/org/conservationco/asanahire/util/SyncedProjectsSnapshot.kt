package org.conservationco.asanahire.util

import com.asana.models.Project
import org.conservationco.asana.asanaContext
import java.time.LocalDateTime

data class SyncedProjectsSnapshot(
    val name: String,
    val source: Project,
    val destination: Project,
) {
    val sourceCount: Int
    val destinationCount: Int
    val time: LocalDateTime

    init {
        var sourceCount = 0
        var destinationCount = 0
        asanaContext {
            sourceCount = source.getTaskCount()
            destinationCount = destination.getTaskCount()
        }
        this.sourceCount = sourceCount
        this.destinationCount = destinationCount
        this.time = LocalDateTime.now()
    }

    fun sourceIsEmpty() = sourceCount == 0
    fun sourceIsNotEmpty() = !sourceIsEmpty()
    fun sourceAndDestinationDiffer() = sourceCount != destinationCount
    fun needsSyncing() = sourceIsNotEmpty() && sourceAndDestinationDiffer()
}

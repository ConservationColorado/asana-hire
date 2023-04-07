package org.conservationco.asanahire.controller

import org.conservationco.asanahire.model.sync.SyncEvent
import org.conservationco.asanahire.service.SyncService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/sync")
class SyncController(
    private val syncService: SyncService,
) {

    @PutMapping("/start/{jobId}")
    fun startSync(@PathVariable jobId: Long) = syncService.startSync(jobId)

    @GetMapping("/{syncId}")
    fun getSyncStatus(@PathVariable syncId: Long): Mono<SyncEvent> = syncService.getSync(syncId)

    @GetMapping
    fun getSyncs(): Mono<MutableList<SyncEvent>> = syncService.getAllSyncs()

}

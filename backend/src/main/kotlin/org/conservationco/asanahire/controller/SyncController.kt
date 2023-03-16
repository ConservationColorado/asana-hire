package org.conservationco.asanahire.controller

import org.conservationco.asanahire.service.SyncService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/sync")
@CrossOrigin(origins = ["http://localhost:3000"])
class SyncController(
    private val syncService: SyncService,
) {

    @PutMapping("/{jobId}")
    suspend fun sync(
        @PathVariable jobId: Long
    ) = syncService.trySync(jobId)

}

package org.conservationco.asanahire.controller

import kotlinx.coroutines.Deferred
import org.conservationco.asana.extensions.events.Event
import org.conservationco.asanahire.domain.ApplicantEvent
import org.conservationco.asanahire.domain.RejectableApplicant
import org.conservationco.asanahire.service.ApplicantService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/applicants")
@CrossOrigin
class ApplicantController(
    private val applicantService: ApplicantService,
) {

    @GetMapping("/{jobId}/reject")
    suspend fun getRejectableApplicants(
        @PathVariable jobId: Long
    ) = applicantService.getAllNeedingRejection(jobId)

    @PutMapping("/reject")
    suspend fun rejectApplicant(
        @RequestBody applicant: RejectableApplicant
    ) = applicantService.rejectApplicant(applicant)

    @PutMapping("/reject-all")
    suspend fun rejectApplicants(
        @RequestBody applicants: List<RejectableApplicant>
    ) = applicants.forEach { applicantService.rejectApplicant(it) }

}

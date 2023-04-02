package org.conservationco.asanahire.controller

import org.conservationco.asanahire.model.applicant.RejectableApplicant
import org.conservationco.asanahire.service.ApplicantService
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/applicants")
class ApplicantController(
    private val applicantService: ApplicantService,
) {

    @GetMapping("/{jobId}/new")
    suspend fun getNewApplicants(
        @PathVariable jobId: Long,
        @RequestBody time: LocalDateTime
    ) = applicantService.getNewApplicants(jobId, time)

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

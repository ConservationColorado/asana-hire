package org.conservationco.asanahire.controller

import org.conservationco.asanahire.domain.RejectableApplicant
import org.conservationco.asanahire.service.ApplicantService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/applicants")
class ApplicantController(
    private val applicantService: ApplicantService,
) {

    @PutMapping("/{jobId}/need_rejection")
    suspend fun getRejectableApplicants(
        @PathVariable jobId: String
    ) = applicantService.getAllNeedingRejection(jobId)

    @PutMapping("/{jobId}/reject")
    suspend fun rejectApplicant(
        @PathVariable jobId: String,
        @RequestBody applicant: RejectableApplicant
    ) = applicantService.rejectApplicant(jobId, applicant)

    @PutMapping("/{jobId}/sync")
    suspend fun sync(
        @PathVariable jobId: String
    ) = applicantService.trySync(jobId)

}

package org.conservationco.asanahire.controller

import org.conservationco.asanahire.domain.RejectableApplicant
import org.conservationco.asanahire.service.ApplicantService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/applicants")
@CrossOrigin
class ApplicantController(
    private val applicantService: ApplicantService,
) {

    @PutMapping("/{jobId}/need_rejection")
    suspend fun getRejectableApplicants(
        @PathVariable jobId: Long
    ) = applicantService.getAllNeedingRejection(jobId)

    @PutMapping("/{jobId}/reject")
    suspend fun rejectApplicant(
        @PathVariable jobId: Long,
        @RequestBody applicant: RejectableApplicant
    ) = applicantService.rejectApplicant(jobId, applicant)

    @PutMapping("{jobId}/batch/reject")
    suspend fun rejectApplicants(
        @PathVariable jobId: Long,
        @RequestBody applicants: List<RejectableApplicant>
    )  {
        for (applicant in applicants) applicantService.rejectApplicant(jobId, applicant)
    }

    @PutMapping("/{jobId}/sync")
    suspend fun sync(
        @PathVariable jobId: Long
    ) = applicantService.trySync(jobId)

}

package org.conservationco.asanahire.controller

import org.conservationco.asanahire.service.ApplicantService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/applicants")
class ApplicantController(
    private val applicantService: ApplicantService,
) {

    @PutMapping("/{jobId}/reject")
    suspend fun rejectApplicants(@PathVariable jobId: String) = applicantService.getAllNeedingRejection(jobId)

    @PutMapping("/{jobId}/sync")
    suspend fun sync(@PathVariable jobId: String) {
        applicantService.trySync(jobId)
    }

}
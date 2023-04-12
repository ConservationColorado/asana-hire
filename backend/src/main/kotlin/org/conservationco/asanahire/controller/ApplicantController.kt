package org.conservationco.asanahire.controller

import org.conservationco.asanahire.model.applicant.RejectableApplicant
import org.conservationco.asanahire.service.ApplicantService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@RestController
@RequestMapping("/applicants")
class ApplicantController(
    private val applicantService: ApplicantService,
) {

    @GetMapping("/{jobId}/new")
    fun getNewApplicants(
        @PathVariable jobId: Long,
        @RequestBody time: LocalDateTime
    ) = applicantService.getNewApplicants(jobId, time)

    @GetMapping("/{jobId}/reject")
    fun getRejectableApplicants(
        @PathVariable jobId: Long
    ) = applicantService.getAllNeedingRejection(jobId)

    @PutMapping("/reject")
    fun rejectApplicant(
        @RequestBody applicant: RejectableApplicant
    ) = applicantService.rejectApplicant(applicant)

    @PutMapping("/reject-all")
    fun rejectApplicants(
        @RequestBody applicants: List<RejectableApplicant>
    ) = Flux
        .fromIterable(applicants)
        .flatMap { applicantService.rejectApplicant(it) }

}

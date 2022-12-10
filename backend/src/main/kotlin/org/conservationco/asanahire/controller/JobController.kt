package org.conservationco.asanahire.controller

import org.conservationco.asanahire.domain.Job
import org.conservationco.asanahire.service.JobService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/jobs")
@CrossOrigin
class JobController(
    private val jobService: JobService,
) {

    @GetMapping
    fun getJobs(): Iterable<Job> = jobService.getJobs()

}
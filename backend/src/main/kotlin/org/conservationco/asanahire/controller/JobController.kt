package org.conservationco.asanahire.controller

import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.service.JobService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/jobs")
@CrossOrigin
class JobController(
    private val jobService: JobService,
) {

    @GetMapping
    suspend fun getJobs(): Iterable<Job> = jobService.getJobs()

    @GetMapping("/{jobId}")
    suspend fun getJob(@PathVariable jobId: Long): Job = jobService.getJob(jobId)

}
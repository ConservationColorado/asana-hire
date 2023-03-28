package org.conservationco.asanahire.controller

import org.conservationco.asanahire.model.job.Job
import org.conservationco.asanahire.service.JobService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/jobs")
class JobController(
    private val jobService: JobService,
) {

    @GetMapping
    fun getJobs(): Mono<List<Job>> = jobService.getLocalOrFetchJobs()

    @GetMapping("/{jobId}")
    fun getJob(@PathVariable jobId: Long): Mono<Job> = jobService.getLocalOrFetchJob(jobId)

}

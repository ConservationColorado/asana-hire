package org.conservationco.asanahire.exception

class JobNotFoundException(jobId: Long) : RuntimeException("Job with ID $jobId not found!")

package org.conservationco.exception

class JobNotFoundException(jobId: Long) : RuntimeException("Job with ID $jobId not found!")

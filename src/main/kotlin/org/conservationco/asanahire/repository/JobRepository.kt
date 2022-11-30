package org.conservationco.asanahire.repository

import org.conservationco.asanahire.domain.Job
import org.springframework.data.repository.CrudRepository

interface JobRepository : CrudRepository<Job, String>

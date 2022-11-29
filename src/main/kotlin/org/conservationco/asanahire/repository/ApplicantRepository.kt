package org.conservationco.asanahire.repository

import org.conservationco.asanahire.domain.Applicant
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicantRepository<T : Applicant> : CrudRepository<T, String> {
    fun isEmpty(): Boolean = count() == 0L
}

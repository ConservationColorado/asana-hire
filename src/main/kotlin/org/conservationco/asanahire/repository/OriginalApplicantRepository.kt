package org.conservationco.asanahire.repository

import org.conservationco.asanahire.domain.OriginalApplicant
import org.springframework.stereotype.Repository

@Repository
internal interface OriginalApplicantRepository : ApplicantRepository<OriginalApplicant>

package com.sahe.itera.domain.usecase.subject

import com.sahe.itera.domain.repository.SubjectRepository
import javax.inject.Inject

class UnarchiveSubjectUseCase @Inject constructor(private val repo: SubjectRepository) {
    suspend operator fun invoke(subject: com.sahe.itera.domain.model.Subject) =
        repo.updateSubject(subject.copy(isArchived = false, archivedAt = null))
}
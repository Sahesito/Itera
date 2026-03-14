package com.sahe.itera.domain.usecase.subject

import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.repository.SubjectRepository
import jakarta.inject.Inject

class UpdateSubjectUseCase @Inject constructor(
    private val repository: SubjectRepository
) {
    suspend operator fun invoke(subject: Subject) = repository.updateSubject(subject)
}
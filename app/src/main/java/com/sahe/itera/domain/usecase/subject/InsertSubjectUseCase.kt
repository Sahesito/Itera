package com.sahe.itera.domain.usecase.subject

import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.repository.SubjectRepository
import javax.inject.Inject

class InsertSubjectUseCase @Inject constructor(private val repo: SubjectRepository) {
    suspend operator fun invoke(subject: Subject): Long = repo.insertSubject(subject)
}
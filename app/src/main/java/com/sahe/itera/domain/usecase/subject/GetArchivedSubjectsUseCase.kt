package com.sahe.itera.domain.usecase.subject

import com.sahe.itera.domain.repository.SubjectRepository
import javax.inject.Inject

class GetArchivedSubjectsUseCase @Inject constructor(private val repo: SubjectRepository) {
    operator fun invoke() = repo.getArchivedSubjects()
}
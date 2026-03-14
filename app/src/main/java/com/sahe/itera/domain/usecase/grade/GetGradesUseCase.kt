package com.sahe.itera.domain.usecase.grade

import com.sahe.itera.domain.repository.GradeRepository
import javax.inject.Inject

class GetGradesUseCase @Inject constructor(private val repo: GradeRepository) {
    operator fun invoke(subjectId: Long) = repo.getBySubject(subjectId)
}
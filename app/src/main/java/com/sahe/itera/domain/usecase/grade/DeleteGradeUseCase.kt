package com.sahe.itera.domain.usecase.grade

import com.sahe.itera.domain.repository.GradeRepository
import com.sahe.itera.domain.model.Grade
import javax.inject.Inject

class DeleteGradeUseCase @Inject constructor(private val repo: GradeRepository) {
    suspend operator fun invoke(grade: Grade) = repo.delete(grade)
}
package com.sahe.itera.domain.usecase.expositions

import com.sahe.itera.domain.model.Exposition
import com.sahe.itera.domain.repository.ExpositionRepository
import javax.inject.Inject

class DeleteExpositionUseCase @Inject constructor(private val repo: ExpositionRepository) {
    suspend operator fun invoke(exposition: Exposition) = repo.delete(exposition)
}
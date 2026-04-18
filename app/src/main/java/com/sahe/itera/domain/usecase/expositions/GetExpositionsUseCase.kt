package com.sahe.itera.domain.usecase.expositions

import com.sahe.itera.domain.repository.ExpositionRepository
import javax.inject.Inject

class GetExpositionsUseCase @Inject constructor(private val repo: ExpositionRepository) {
    operator fun invoke() = repo.getAll()
}
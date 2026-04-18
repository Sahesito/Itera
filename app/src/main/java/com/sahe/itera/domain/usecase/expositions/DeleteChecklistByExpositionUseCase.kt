package com.sahe.itera.domain.usecase.expositions

import com.sahe.itera.domain.repository.ChecklistRepository
import javax.inject.Inject

class DeleteChecklistByExpositionUseCase @Inject constructor(
    private val repo: ChecklistRepository
) {
    suspend operator fun invoke(expositionId: Long) = repo.deleteByExposition(expositionId)
}
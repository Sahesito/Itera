package com.sahe.itera.domain.usecase.checklist

import com.sahe.itera.domain.model.ChecklistItem
import com.sahe.itera.domain.repository.ChecklistRepository
import javax.inject.Inject

class DeleteChecklistItemUseCase @Inject constructor(private val repo: ChecklistRepository) {
    suspend operator fun invoke(item: ChecklistItem) = repo.delete(item)
}
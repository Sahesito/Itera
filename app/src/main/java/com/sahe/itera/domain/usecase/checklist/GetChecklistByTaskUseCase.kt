package com.sahe.itera.domain.usecase.checklist

import com.sahe.itera.domain.repository.ChecklistRepository
import javax.inject.Inject

class GetChecklistByTaskUseCase @Inject constructor(private val repo: ChecklistRepository) {
    operator fun invoke(taskId: Long) = repo.getByTask(taskId)
}
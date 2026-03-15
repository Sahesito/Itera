package com.sahe.itera.domain.usecase.schedule

import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.repository.ScheduleRepository
import javax.inject.Inject

class DeleteScheduleBlockUseCase @Inject constructor(private val repo: ScheduleRepository) {
    suspend operator fun invoke(block: ScheduleBlock) = repo.delete(block)
}
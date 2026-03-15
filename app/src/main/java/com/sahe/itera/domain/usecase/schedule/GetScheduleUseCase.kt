package com.sahe.itera.domain.usecase.schedule

import com.sahe.itera.domain.repository.ScheduleRepository
import javax.inject.Inject

class GetScheduleUseCase @Inject constructor(private val repo: ScheduleRepository) {
    operator fun invoke() = repo.getAll()
}
package com.sahe.itera.domain.usecase.task

import com.sahe.itera.domain.repository.TaskRepository
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke() = repository.getTasks()
}
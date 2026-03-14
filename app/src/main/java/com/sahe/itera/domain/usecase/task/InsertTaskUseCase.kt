package com.sahe.itera.domain.usecase.task

import com.sahe.itera.domain.model.Task
import com.sahe.itera.domain.repository.TaskRepository
import javax.inject.Inject

class InsertTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long = repository.insertTask(task)
}
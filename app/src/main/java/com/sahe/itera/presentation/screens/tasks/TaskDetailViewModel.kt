package com.sahe.itera.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.core.worker.ReminderScheduler
import com.sahe.itera.domain.model.Task
import com.sahe.itera.domain.usecase.task.DeleteTaskUseCase
import com.sahe.itera.domain.usecase.task.GetTasksUseCase
import com.sahe.itera.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTasks: GetTasksUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    private var _taskId: Long = -1

    private val _task = MutableStateFlow<Task?>(null)
    val task: StateFlow<Task?> = _task.asStateFlow()

    fun loadTask(taskId: Long) {
        if (_taskId == taskId) return
        _taskId = taskId
        viewModelScope.launch {
            getTasks()
                .map { list -> list.firstOrNull { it.id == taskId } }
                .collect { _task.value = it }
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch { updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun delete(task: Task, onDone: () -> Unit) {
        viewModelScope.launch {
            deleteTask(task)
            scheduler.cancel(task.id)
            onDone()
        }
    }

    fun update(task: Task) {
        viewModelScope.launch { updateTask(task) }
    }
}
package com.sahe.itera.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.core.worker.ReminderScheduler
import com.sahe.itera.domain.model.Task
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import com.sahe.itera.domain.usecase.task.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter { ALL, PENDING, COMPLETED }

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val getTasks: GetTasksUseCase,
    private val insertTask: InsertTaskUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val scheduler: ReminderScheduler,
    getSubjects: GetSubjectsUseCase
) : ViewModel() {

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter.asStateFlow()

    val tasks: StateFlow<List<Task>> = combine(
        getTasks(), _filter
    ) { list, filter ->
        when (filter) {
            TaskFilter.ALL       -> list
            TaskFilter.PENDING   -> list.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> list.filter { it.isCompleted }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setFilter(f: TaskFilter) { _filter.value = f }

    fun insert(task: Task) {
        viewModelScope.launch {
            val id = insertTask(task)
            if (task.hasReminder) scheduler.schedule(task.copy(id = id))
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch { updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            deleteTask(task)
            scheduler.cancel(task.id)
        }
    }
}
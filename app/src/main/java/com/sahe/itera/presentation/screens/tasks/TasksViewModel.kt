package com.sahe.itera.presentation.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.core.worker.ReminderScheduler
import com.sahe.itera.domain.model.ChecklistItem
import com.sahe.itera.domain.model.Exposition
import com.sahe.itera.domain.model.Task
import com.sahe.itera.domain.usecase.checklist.*
import com.sahe.itera.domain.usecase.expositions.*
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import com.sahe.itera.domain.usecase.task.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter { ALL, PENDING, COMPLETED }

@HiltViewModel
class TasksViewModel @Inject constructor(
    getTasks: GetTasksUseCase,
    getExpositions: GetExpositionsUseCase,
    private val insertTask: InsertTaskUseCase,
    private val updateTask: UpdateTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val insertExposition: InsertExpositionUseCase,
    private val updateExposition: UpdateExpositionUseCase,
    private val deleteExposition: DeleteExpositionUseCase,
    private val insertChecklistItem: InsertChecklistItemUseCase,
    private val deleteChecklistByExposition: DeleteChecklistByExpositionUseCase,
    private val getChecklistByExposition: GetChecklistByExpositionUseCase,
    private val scheduler: ReminderScheduler,
    getSubjects: GetSubjectsUseCase
) : ViewModel() {

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _taskFilter       = MutableStateFlow(TaskFilter.ALL)
    private val _examFilter       = MutableStateFlow(TaskFilter.ALL)
    private val _expositionFilter = MutableStateFlow(TaskFilter.ALL)

    val taskFilter: StateFlow<TaskFilter>       = _taskFilter.asStateFlow()
    val examFilter: StateFlow<TaskFilter>       = _examFilter.asStateFlow()
    val expositionFilter: StateFlow<TaskFilter> = _expositionFilter.asStateFlow()

    val tasks: StateFlow<List<Task>> = combine(getTasks(), _taskFilter) { list, filter ->
        list.filter { !it.isExam }.let { tasks ->
            when (filter) {
                TaskFilter.ALL       -> tasks
                TaskFilter.PENDING   -> tasks.filter { !it.isCompleted }
                TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val exams: StateFlow<List<Task>> = combine(getTasks(), _examFilter) { list, filter ->
        list.filter { it.isExam }.let { exams ->
            when (filter) {
                TaskFilter.ALL       -> exams.sortedBy { it.dueDateTime }
                TaskFilter.PENDING   -> exams.filter { !it.isCompleted }.sortedBy { it.dueDateTime }
                TaskFilter.COMPLETED -> exams.filter { it.isCompleted }.sortedByDescending { it.dueDateTime }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val expositions: StateFlow<List<Exposition>> = combine(getExpositions(), _expositionFilter) { list, filter ->
        when (filter) {
            TaskFilter.ALL       -> list
            TaskFilter.PENDING   -> list.filter { !it.isCompleted }.sortedBy { it.dueDateTime }
            TaskFilter.COMPLETED -> list.filter { it.isCompleted }.sortedByDescending { it.dueDateTime }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _checklistCache = mutableMapOf<Long, StateFlow<List<ChecklistItem>>>()

    fun getChecklistForExposition(expositionId: Long): StateFlow<List<ChecklistItem>> =
        _checklistCache.getOrPut(expositionId) {
            getChecklistByExposition(expositionId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        }

    fun setTaskFilter(f: TaskFilter)       { _taskFilter.value = f }
    fun setExamFilter(f: TaskFilter)       { _examFilter.value = f }
    fun setExpositionFilter(f: TaskFilter) { _expositionFilter.value = f }

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

    fun insertExposition(exposition: Exposition, checklistItems: List<String>) {
        viewModelScope.launch {
            val id = insertExposition(exposition)
            checklistItems.forEach { material ->
                insertChecklistItem(
                    ChecklistItem(
                        material           = material,
                        subjectId          = exposition.subjectId,
                        linkedExpositionId = id
                    )
                )
            }
        }
    }

    fun toggleExpositionComplete(exposition: Exposition) {
        viewModelScope.launch {
            updateExposition(exposition.copy(isCompleted = !exposition.isCompleted))
        }
    }

    fun removeExposition(exposition: Exposition) {
        viewModelScope.launch {
            deleteChecklistByExposition(exposition.id)
            deleteExposition(exposition)
        }
    }

    fun toggleChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            insertChecklistItem(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteChecklistItem(item: ChecklistItem) {
        viewModelScope.launch { deleteChecklistItem(item) }
    }

}
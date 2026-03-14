package com.sahe.itera.presentation.screens.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.model.Task
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
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
class SubjectDetailViewModel @Inject constructor(
    private val getSubjects: GetSubjectsUseCase,
    private val getTasks: GetTasksUseCase,
    private val updateTask: UpdateTaskUseCase
) : ViewModel() {

    private val _subject = MutableStateFlow<Subject?>(null)
    val subject: StateFlow<Subject?> = _subject.asStateFlow()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun load(subjectId: Long) {
        viewModelScope.launch {
            getSubjects().map { list -> list.firstOrNull { it.id == subjectId } }
                .collect { _subject.value = it }
        }
        viewModelScope.launch {
            getTasks().map { list -> list.filter { it.subjectId == subjectId } }
                .collect { _tasks.value = it }
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch { updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }
}
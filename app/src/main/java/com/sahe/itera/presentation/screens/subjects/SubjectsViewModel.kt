package com.sahe.itera.presentation.screens.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.usecase.schedule.InsertScheduleBlockUseCase
import com.sahe.itera.domain.usecase.subject.DeleteSubjectUseCase
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import com.sahe.itera.domain.usecase.subject.InsertSubjectUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
    private val insertSubject: InsertSubjectUseCase,
    private val deleteSubject: DeleteSubjectUseCase,
    private val insertBlock: InsertScheduleBlockUseCase
) : ViewModel() {

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insert(subject: Subject, onInserted: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = insertSubject(subject)
            onInserted(id)
        }
    }

    fun insertScheduleBlock(block: ScheduleBlock) {
        viewModelScope.launch { insertBlock(block) }
    }

    fun delete(subject: Subject) {
        viewModelScope.launch { deleteSubject(subject) }
    }
}
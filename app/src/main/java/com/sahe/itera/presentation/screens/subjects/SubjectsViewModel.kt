package com.sahe.itera.presentation.screens.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.usecase.schedule.InsertScheduleBlockUseCase
import com.sahe.itera.domain.usecase.subject.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
    getArchivedSubjects: GetArchivedSubjectsUseCase,
    private val insertSubject: InsertSubjectUseCase,
    private val updateSubject: UpdateSubjectUseCase,
    private val deleteSubject: DeleteSubjectUseCase,
    private val archiveSubject: ArchiveSubjectUseCase,
    private val unarchiveSubject: UnarchiveSubjectUseCase,
    private val insertBlock: InsertScheduleBlockUseCase
) : ViewModel() {

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val archivedSubjects = getArchivedSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insert(subject: Subject, onInserted: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = insertSubject(subject)
            onInserted(id)
        }
    }

    fun update(subject: Subject) {
        viewModelScope.launch { updateSubject(subject) }
    }

    fun insertScheduleBlock(block: ScheduleBlock) {
        viewModelScope.launch { insertBlock(block) }
    }

    fun delete(subject: Subject) {
        viewModelScope.launch { deleteSubject(subject) }
    }

    fun archive(subject: Subject) {
        viewModelScope.launch { archiveSubject(subject) }
    }

    fun unarchive(subject: Subject) {
        viewModelScope.launch { unarchiveSubject(subject) }
    }
}
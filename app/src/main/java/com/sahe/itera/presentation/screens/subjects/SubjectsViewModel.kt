package com.sahe.itera.presentation.screens.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.Subject
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
    private val getSubjects: GetSubjectsUseCase,
    private val insertSubject: InsertSubjectUseCase,
    private val deleteSubject: DeleteSubjectUseCase
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = getSubjects()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun insert(subject: Subject) {
        viewModelScope.launch { insertSubject(subject) }
    }

    fun delete(subject: Subject) {
        viewModelScope.launch { deleteSubject(subject) }
    }
}
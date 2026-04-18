package com.sahe.itera.presentation.screens.checklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.ChecklistItem
import com.sahe.itera.domain.usecase.checklist.*
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    getChecklist: GetChecklistUseCase,
    getSubjects: GetSubjectsUseCase,
    private val insertItem: InsertChecklistItemUseCase,
    private val updateItem: UpdateChecklistItemUseCase,
    private val deleteItem: DeleteChecklistItemUseCase
) : ViewModel() {

    val items = getChecklist()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insert(item: ChecklistItem) {
        viewModelScope.launch { insertItem(item) }
    }

    fun toggle(item: ChecklistItem) {
        viewModelScope.launch { updateItem(item.copy(isChecked = !item.isChecked)) }
    }

    fun delete(item: ChecklistItem) {
        viewModelScope.launch { deleteItem(item) }
    }
}
package com.sahe.itera.presentation.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.usecase.schedule.*
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    getSchedule: GetScheduleUseCase,
    getSubjects: GetSubjectsUseCase,
    private val insertBlock: InsertScheduleBlockUseCase,
    private val deleteBlock: DeleteScheduleBlockUseCase
) : ViewModel() {

    val blocks = getSchedule()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insert(block: ScheduleBlock) {
        viewModelScope.launch { insertBlock(block) }
    }

    fun delete(block: ScheduleBlock) {
        viewModelScope.launch { deleteBlock(block) }
    }
}
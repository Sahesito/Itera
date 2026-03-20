package com.sahe.itera.presentation.screens.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahe.itera.domain.model.Attendance
import com.sahe.itera.domain.usecase.attendance.*
import com.sahe.itera.domain.usecase.subject.GetSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    getSubjects: GetSubjectsUseCase,
    private val getAttendance: GetAttendanceUseCase,
    private val insertAttendance: InsertAttendanceUseCase,
    private val deleteAttendance: DeleteAttendanceUseCase
) : ViewModel() {

    val subjects = getSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _attendanceCache = mutableMapOf<Long, StateFlow<List<Attendance>>>()

    fun getAttendanceForSubject(subjectId: Long): StateFlow<List<Attendance>> {
        return _attendanceCache.getOrPut(subjectId) {
            getAttendance(subjectId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        }
    }

    fun insert(attendance: Attendance) {
        viewModelScope.launch { insertAttendance(attendance) }
    }

    fun delete(attendance: Attendance) {
        viewModelScope.launch { deleteAttendance(attendance) }
    }
}
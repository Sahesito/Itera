package com.sahe.itera.domain.repository

import com.sahe.itera.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getBySubject(subjectId: Long): Flow<List<Attendance>>
    suspend fun insert(attendance: Attendance)
    suspend fun delete(attendance: Attendance)
}
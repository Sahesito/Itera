package com.sahe.itera.data.repositories

import com.sahe.itera.data.database.dao.AttendanceDao
import com.sahe.itera.data.mappers.toDomain
import com.sahe.itera.data.mappers.toEntity
import com.sahe.itera.domain.model.Attendance
import com.sahe.itera.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val dao: AttendanceDao
) : AttendanceRepository {
    override fun getBySubject(subjectId: Long) =
        dao.getBySubject(subjectId).map { list -> list.map { it.toDomain() } }
    override suspend fun insert(attendance: Attendance) = dao.insert(attendance.toEntity())
    override suspend fun delete(attendance: Attendance) = dao.delete(attendance.toEntity())
}
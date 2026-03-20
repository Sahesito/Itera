package com.sahe.itera.domain.usecase.attendance

import com.sahe.itera.domain.model.Attendance
import com.sahe.itera.domain.repository.AttendanceRepository
import javax.inject.Inject

class InsertAttendanceUseCase @Inject constructor(private val repo: AttendanceRepository) {
    suspend operator fun invoke(attendance: Attendance) = repo.insert(attendance)
}

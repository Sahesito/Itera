package com.sahe.itera.domain.usecase.attendance

import com.sahe.itera.domain.model.Attendance
import com.sahe.itera.domain.repository.AttendanceRepository
import javax.inject.Inject

class DeleteAttendanceUseCase @Inject constructor(private val repo: AttendanceRepository) {
    suspend operator fun invoke(attendance: Attendance) = repo.delete(attendance)
}
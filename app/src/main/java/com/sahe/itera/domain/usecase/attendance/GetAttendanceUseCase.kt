package com.sahe.itera.domain.usecase.attendance

import com.sahe.itera.domain.repository.AttendanceRepository
import javax.inject.Inject

class GetAttendanceUseCase @Inject constructor(private val repo: AttendanceRepository) {
    operator fun invoke(subjectId: Long) = repo.getBySubject(subjectId)
}

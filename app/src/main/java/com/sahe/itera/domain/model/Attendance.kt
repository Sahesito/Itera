package com.sahe.itera.domain.model

import java.time.LocalDate

data class Attendance(
    val id: Long = 0,
    val subjectId: Long,
    val type: AttendanceType,
    val date: LocalDate,
    val note: String = ""
)
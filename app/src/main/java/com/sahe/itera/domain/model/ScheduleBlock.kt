package com.sahe.itera.domain.model

data class ScheduleBlock(
    val id: Long = 0,
    val subjectId: Long,
    val subjectName: String = "",
    val subjectColor: String = "",
    val dayOfWeek: Int,
    val startHour: Int,
    val endHour: Int
)
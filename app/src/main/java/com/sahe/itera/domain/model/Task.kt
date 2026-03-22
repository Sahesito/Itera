package com.sahe.itera.domain.model

import java.time.LocalDateTime

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val subjectId: Long? = null,
    val subjectName: String? = null,
    val subjectColor: String? = null,
    val dueDateTime: LocalDateTime? = null,
    val isCompleted: Boolean = false,
    val hasReminder: Boolean = false,
    val priority: Priority = Priority.NORMAL,
    val isExam: Boolean = false
)
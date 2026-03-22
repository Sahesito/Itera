package com.sahe.itera.data.database.entities

data class TaskWithSubject(
    val id: Long,
    val title: String,
    val description: String,
    val subjectId: Long?,
    val dueDateTime: Long?,
    val isCompleted: Boolean,
    val hasReminder: Boolean,
    val subjectName: String?,
    val subjectColor: String?,
    val priority: String = "NORMAL",
    val isExam: Boolean = false
)
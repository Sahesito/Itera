package com.sahe.itera.data.database.entities

data class ExpositionWithSubject(
    val id: Long,
    val topic: String,
    val subjectId: Long,
    val dueDateTime: Long?,
    val isCompleted: Boolean,
    val subjectName: String?,
    val subjectColor: String?
)
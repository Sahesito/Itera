package com.sahe.itera.data.database.entities

data class ChecklistItemWithSubject(
    val id: Long,
    val material: String,
    val subjectId: Long?,
    val dueDate: Long?,
    val isChecked: Boolean,
    val linkedTaskId: Long?,
    val linkedExpositionId: Long?,
    val subjectName: String?,
    val subjectColor: String?
)
package com.sahe.itera.domain.model

import java.time.LocalDateTime

data class Exposition(
    val id: Long = 0,
    val topic: String,
    val subjectId: Long,
    val subjectName: String? = null,
    val subjectColor: String? = null,
    val dueDateTime: LocalDateTime? = null,
    val isCompleted: Boolean = false,
    val checklistItems: List<ChecklistItem> = emptyList()
)
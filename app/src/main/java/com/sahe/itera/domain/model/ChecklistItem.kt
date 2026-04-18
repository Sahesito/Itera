package com.sahe.itera.domain.model

import java.time.LocalDate

data class ChecklistItem(
    val id: Long = 0,
    val material: String,
    val subjectId: Long? = null,
    val subjectName: String? = null,
    val subjectColor: String? = null,
    val dueDate: LocalDate? = null,
    val isChecked: Boolean = false,
    val linkedTaskId: Long? = null,
    val linkedExpositionId: Long? = null
)
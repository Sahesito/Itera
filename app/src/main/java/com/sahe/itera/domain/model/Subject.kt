package com.sahe.itera.domain.model

data class Subject(
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val credits: Int = 0,
    val targetGrade: Float = 11f,
    val currentAverage: Float? = null,
    val teacher: String = "",
    val maxAbsences: Int = 0,
    val maxTardiness: Int = 0,
    val isArchived: Boolean = false,
    val archivedAt: Long? = null
)
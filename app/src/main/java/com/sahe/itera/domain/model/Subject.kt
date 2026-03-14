package com.sahe.itera.domain.model

data class Subject(
    val id: Long = 0,
    val name: String,
    val colorHex: String,      // ej: "#5685D5"
    val credits: Int = 0,
    val targetGrade: Float = 11f,
    val currentAverage: Float? = null,
    val teacher: String = ""
)
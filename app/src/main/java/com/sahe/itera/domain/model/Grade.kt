package com.sahe.itera.domain.model

data class Grade(
    val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val weight: Float,
    val score: Float? = null,
    val maxScore: Float = 20f
)
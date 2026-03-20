package com.sahe.itera.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val credits: Int,
    val targetGrade: Float,
    val currentAverage: Float?,
    val teacher: String = "",
    val maxAbsences: Int = 0,
    val maxTardiness: Int = 0
)
package com.sahe.itera.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val subjectId: Long?,
    val dueDateTime: Long?,
    val isCompleted: Boolean,
    val hasReminder: Boolean,
    val priority: String = "NORMAL",
    val isExam: Boolean = false
)
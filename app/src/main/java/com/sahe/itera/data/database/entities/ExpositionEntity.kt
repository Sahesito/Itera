package com.sahe.itera.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "expositions",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpositionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topic: String,
    val subjectId: Long,
    val dueDateTime: Long? = null,
    val isCompleted: Boolean = false
)
package com.sahe.itera.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val material: String,
    val subjectId: Long? = null,
    val dueDate: Long? = null,
    val isChecked: Boolean = false,
    val linkedTaskId: Long? = null,
    val linkedExpositionId: Long? = null
)
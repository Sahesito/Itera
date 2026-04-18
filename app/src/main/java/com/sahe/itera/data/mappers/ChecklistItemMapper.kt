package com.sahe.itera.data.mappers


import com.sahe.itera.data.database.entities.ChecklistItemEntity
import com.sahe.itera.data.database.entities.ChecklistItemWithSubject
import com.sahe.itera.domain.model.ChecklistItem
import java.time.Instant
import java.time.ZoneId

fun ChecklistItemWithSubject.toDomain() = ChecklistItem(
    id                  = id,
    material            = material,
    subjectId           = subjectId,
    subjectName         = subjectName,
    subjectColor        = subjectColor,
    dueDate             = dueDate?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    },
    isChecked           = isChecked,
    linkedTaskId        = linkedTaskId,
    linkedExpositionId  = linkedExpositionId
)

fun ChecklistItem.toEntity() = ChecklistItemEntity(
    id                  = id,
    material            = material,
    subjectId           = subjectId,
    dueDate             = dueDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
    isChecked           = isChecked,
    linkedTaskId        = linkedTaskId,
    linkedExpositionId  = linkedExpositionId
)
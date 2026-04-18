package com.sahe.itera.data.mappers


import com.sahe.itera.data.database.entities.ExpositionEntity
import com.sahe.itera.data.database.entities.ExpositionWithSubject
import com.sahe.itera.domain.model.Exposition
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun ExpositionWithSubject.toDomain() = Exposition(
    id           = id,
    topic        = topic,
    subjectId    = subjectId,
    subjectName  = subjectName,
    subjectColor = subjectColor,
    dueDateTime  = dueDateTime?.let {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
    },
    isCompleted  = isCompleted
)

fun Exposition.toEntity() = ExpositionEntity(
    id          = id,
    topic       = topic,
    subjectId   = subjectId,
    dueDateTime = dueDateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
    isCompleted = isCompleted
)
package com.sahe.itera.data.mappers

import com.sahe.itera.data.database.entities.ScheduleBlockEntity
import com.sahe.itera.domain.model.ScheduleBlock

fun ScheduleBlockEntity.toDomain(
    subjectName: String = "",
    subjectColor: String = ""
) = ScheduleBlock(
    id           = id,
    subjectId    = subjectId,
    subjectName  = subjectName,
    subjectColor = subjectColor,
    dayOfWeek    = dayOfWeek,
    startHour    = startHour,
    endHour      = endHour
)

fun ScheduleBlock.toEntity() = ScheduleBlockEntity(
    id        = id,
    subjectId = subjectId,
    dayOfWeek = dayOfWeek,
    startHour = startHour,
    endHour   = endHour
)
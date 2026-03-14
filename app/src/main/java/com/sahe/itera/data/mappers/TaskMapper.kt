package com.sahe.itera.data.mappers

import com.sahe.itera.data.database.dao.TaskWithSubject
import com.sahe.itera.data.database.entities.TaskEntity
import com.sahe.itera.domain.model.Priority
import com.sahe.itera.domain.model.Task
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun TaskWithSubject.toDomain() = Task(
    id            = id,
    title         = title,
    description   = description,
    subjectId     = subjectId,
    subjectName   = subjectName,
    subjectColor  = subjectColor,
    dueDateTime   = dueDateTime?.let {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
    },
    isCompleted   = isCompleted,
    hasReminder   = hasReminder,
    priority      = runCatching { Priority.valueOf(priority) }.getOrDefault(Priority.NORMAL)
)

fun Task.toEntity() = TaskEntity(
    id          = id,
    title       = title,
    description = description,
    subjectId   = subjectId,
    dueDateTime = dueDateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli(),
    isCompleted = isCompleted,
    hasReminder = hasReminder,
    priority    = priority.name
)
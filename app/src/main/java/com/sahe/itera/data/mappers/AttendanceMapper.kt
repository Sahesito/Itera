package com.sahe.itera.data.mappers

import com.sahe.itera.data.database.entities.AttendanceEntity
import com.sahe.itera.domain.model.Attendance
import com.sahe.itera.domain.model.AttendanceType
import java.time.Instant
import java.time.ZoneId

fun AttendanceEntity.toDomain() = Attendance(
    id        = id,
    subjectId = subjectId,
    type      = AttendanceType.valueOf(type),
    date      = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate(),
    note      = note
)

fun Attendance.toEntity() = AttendanceEntity(
    id        = id,
    subjectId = subjectId,
    type      = type.name,
    date      = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    note      = note
)
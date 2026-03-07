package com.sahe.itera.data.mappers

import com.sahe.itera.data.database.entities.SubjectEntity
import com.sahe.itera.domain.model.Subject

fun SubjectEntity.toDomain() = Subject(
    id            = id,
    name          = name,
    colorHex      = colorHex,
    credits       = credits,
    targetGrade   = targetGrade,
    currentAverage = currentAverage
)

fun Subject.toEntity() = SubjectEntity(
    id            = id,
    name          = name,
    colorHex      = colorHex,
    credits       = credits,
    targetGrade   = targetGrade,
    currentAverage = currentAverage
)
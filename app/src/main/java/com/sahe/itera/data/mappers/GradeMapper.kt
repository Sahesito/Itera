package com.sahe.itera.data.mappers

import com.sahe.itera.data.database.entities.GradeEntity
import com.sahe.itera.domain.model.Grade

fun GradeEntity.toDomain() = Grade(
    id        = id,
    subjectId = subjectId,
    name      = name,
    weight    = weight,
    score     = score,
    maxScore  = maxScore
)

fun Grade.toEntity() = GradeEntity(
    id        = id,
    subjectId = subjectId,
    name      = name,
    weight    = weight,
    score     = score,
    maxScore  = maxScore
)
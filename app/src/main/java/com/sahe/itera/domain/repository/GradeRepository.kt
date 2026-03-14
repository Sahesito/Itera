package com.sahe.itera.domain.repository

import com.sahe.itera.domain.model.Grade
import kotlinx.coroutines.flow.Flow

interface GradeRepository {
    fun getBySubject(subjectId: Long): Flow<List<Grade>>
    suspend fun insert(grade: Grade)
    suspend fun update(grade: Grade)
    suspend fun delete(grade: Grade)
}
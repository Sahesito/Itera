package com.sahe.itera.domain.repository

import com.sahe.itera.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {
    fun getSubjects(): Flow<List<Subject>>
    suspend fun getSubjectById(id: Long): Subject?
    suspend fun insertSubject(subject: Subject): Long
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(subject: Subject)
}
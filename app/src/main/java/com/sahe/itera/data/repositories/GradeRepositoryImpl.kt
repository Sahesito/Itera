package com.sahe.itera.data.repositories

import com.sahe.itera.data.database.dao.GradeDao
import com.sahe.itera.data.mappers.toDomain
import com.sahe.itera.data.mappers.toEntity
import com.sahe.itera.domain.model.Grade
import com.sahe.itera.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GradeRepositoryImpl @Inject constructor(
    private val dao: GradeDao
) : GradeRepository {
    override fun getBySubject(subjectId: Long) =
        dao.getBySubject(subjectId).map { list -> list.map { it.toDomain() } }
    override suspend fun insert(grade: Grade) = dao.insert(grade.toEntity())
    override suspend fun update(grade: Grade) = dao.update(grade.toEntity())
    override suspend fun delete(grade: Grade) = dao.delete(grade.toEntity())
}
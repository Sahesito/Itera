package com.sahe.itera.data.repositories

import com.sahe.itera.data.database.dao.SubjectDao
import com.sahe.itera.data.mappers.toDomain
import com.sahe.itera.data.mappers.toEntity
import com.sahe.itera.domain.model.Subject
import com.sahe.itera.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val dao: SubjectDao
) : SubjectRepository {

    override fun getSubjects(): Flow<List<Subject>> =
        dao.getSubjects().map { list -> list.map { it.toDomain() } }

    override suspend fun getSubjectById(id: Long): Subject? =
        dao.getSubjectById(id)?.toDomain()

    override suspend fun insertSubject(subject: Subject): Long =
        dao.insertSubject(subject.toEntity())

    override suspend fun updateSubject(subject: Subject) =
        dao.updateSubject(subject.toEntity())

    override suspend fun deleteSubject(subject: Subject) =
        dao.deleteSubject(subject.toEntity())
}
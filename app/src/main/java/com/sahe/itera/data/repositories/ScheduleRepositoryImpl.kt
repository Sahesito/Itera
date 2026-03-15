package com.sahe.itera.data.repositories

import com.sahe.itera.data.database.dao.ScheduleBlockDao
import com.sahe.itera.data.database.dao.SubjectDao
import com.sahe.itera.data.database.entities.ScheduleBlockEntity
import com.sahe.itera.data.database.entities.SubjectEntity
import com.sahe.itera.data.mappers.toDomain
import com.sahe.itera.data.mappers.toEntity
import com.sahe.itera.domain.model.ScheduleBlock
import com.sahe.itera.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val dao: ScheduleBlockDao,
    private val subjectDao: SubjectDao
) : ScheduleRepository {

    override fun getAll(): Flow<List<ScheduleBlock>> =
        combine(
            dao.getAll(),
            subjectDao.getSubjects()
        ) { blocks: List<ScheduleBlockEntity>, subjects: List<SubjectEntity> ->
            blocks.map { block ->
                val subject = subjects.firstOrNull { it.id == block.subjectId }
                block.toDomain(
                    subjectName  = subject?.name ?: "",
                    subjectColor = subject?.colorHex ?: "#9E9E9E"
                )
            }
        }

    override suspend fun insert(block: ScheduleBlock) = dao.insert(block.toEntity())
    override suspend fun delete(block: ScheduleBlock) = dao.delete(block.toEntity())
}
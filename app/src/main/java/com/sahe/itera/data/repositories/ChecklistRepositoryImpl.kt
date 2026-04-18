package com.sahe.itera.data.repositories

import com.sahe.itera.data.database.dao.ChecklistItemDao
import com.sahe.itera.data.mappers.toDomain
import com.sahe.itera.data.mappers.toEntity
import com.sahe.itera.domain.model.ChecklistItem
import com.sahe.itera.domain.repository.ChecklistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChecklistRepositoryImpl @Inject constructor(
    private val dao: ChecklistItemDao
) : ChecklistRepository {
    override fun getAll(): Flow<List<ChecklistItem>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override fun getByExposition(expositionId: Long): Flow<List<ChecklistItem>> =
        dao.getByExposition(expositionId).map { list ->
            list.map { entity ->
                ChecklistItem(
                    id                 = entity.id,
                    material           = entity.material,
                    subjectId          = entity.subjectId,
                    dueDate            = null,
                    isChecked          = entity.isChecked,
                    linkedTaskId       = entity.linkedTaskId,
                    linkedExpositionId = entity.linkedExpositionId
                )
            }
        }

    override fun getByTask(taskId: Long): Flow<List<ChecklistItem>> =
        dao.getByTask(taskId).map { list ->
            list.map { entity ->
                ChecklistItem(
                    id                 = entity.id,
                    material           = entity.material,
                    subjectId          = entity.subjectId,
                    dueDate            = null,
                    isChecked          = entity.isChecked,
                    linkedTaskId       = entity.linkedTaskId,
                    linkedExpositionId = entity.linkedExpositionId
                )
            }
        }

    override suspend fun insert(item: ChecklistItem): Long =
        dao.insert(item.toEntity())

    override suspend fun update(item: ChecklistItem) =
        dao.update(item.toEntity())

    override suspend fun delete(item: ChecklistItem) =
        dao.delete(item.toEntity())

    override suspend fun deleteByExposition(expositionId: Long) =
        dao.deleteByExposition(expositionId)
}
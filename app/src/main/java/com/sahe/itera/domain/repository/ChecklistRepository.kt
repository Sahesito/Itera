package com.sahe.itera.domain.repository

import com.sahe.itera.domain.model.ChecklistItem
import kotlinx.coroutines.flow.Flow

interface ChecklistRepository {
    fun getAll(): Flow<List<ChecklistItem>>
    fun getByExposition(expositionId: Long): Flow<List<ChecklistItem>>
    fun getByTask(taskId: Long): Flow<List<ChecklistItem>>
    suspend fun insert(item: ChecklistItem): Long
    suspend fun update(item: ChecklistItem)
    suspend fun delete(item: ChecklistItem)
    suspend fun deleteByExposition(expositionId: Long)
}
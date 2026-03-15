package com.sahe.itera.domain.repository

import com.sahe.itera.domain.model.ScheduleBlock
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getAll(): Flow<List<ScheduleBlock>>
    suspend fun insert(block: ScheduleBlock)
    suspend fun delete(block: ScheduleBlock)
}
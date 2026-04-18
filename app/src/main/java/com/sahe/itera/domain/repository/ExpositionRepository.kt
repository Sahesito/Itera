package com.sahe.itera.domain.repository

import com.sahe.itera.domain.model.Exposition
import kotlinx.coroutines.flow.Flow

interface ExpositionRepository {
    fun getAll(): Flow<List<Exposition>>
    suspend fun insert(exposition: Exposition): Long
    suspend fun update(exposition: Exposition)
    suspend fun delete(exposition: Exposition)
}
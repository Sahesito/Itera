package com.sahe.itera.data.repositories

import com.sahe.itera.data.database.dao.ExpositionDao
import com.sahe.itera.data.mappers.toDomain
import com.sahe.itera.data.mappers.toEntity
import com.sahe.itera.domain.model.Exposition
import com.sahe.itera.domain.repository.ExpositionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpositionRepositoryImpl @Inject constructor(
    private val dao: ExpositionDao
) : ExpositionRepository {
    override fun getAll(): Flow<List<Exposition>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun insert(exposition: Exposition): Long =
        dao.insert(exposition.toEntity())

    override suspend fun update(exposition: Exposition) =
        dao.update(exposition.toEntity())

    override suspend fun delete(exposition: Exposition) =
        dao.delete(exposition.toEntity())
}
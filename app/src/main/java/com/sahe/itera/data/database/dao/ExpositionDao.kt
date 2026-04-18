package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.ExpositionEntity
import com.sahe.itera.data.database.entities.ExpositionWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpositionDao {
    @Query("""
        SELECT e.*, s.name as subjectName, s.colorHex as subjectColor
        FROM expositions e
        LEFT JOIN subjects s ON e.subjectId = s.id
        ORDER BY e.isCompleted ASC, e.dueDateTime ASC
    """)
    fun getAll(): Flow<List<ExpositionWithSubject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exposition: ExpositionEntity): Long

    @Update
    suspend fun update(exposition: ExpositionEntity)

    @Delete
    suspend fun delete(exposition: ExpositionEntity)
}
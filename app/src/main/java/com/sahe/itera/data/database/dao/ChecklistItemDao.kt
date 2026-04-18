package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.ChecklistItemEntity
import com.sahe.itera.data.database.entities.ChecklistItemWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Query("""
        SELECT c.*, s.name as subjectName, s.colorHex as subjectColor
        FROM checklist_items c
        LEFT JOIN subjects s ON c.subjectId = s.id
        ORDER BY c.isChecked ASC, c.dueDate ASC
    """)
    fun getAll(): Flow<List<ChecklistItemWithSubject>>

    @Query("SELECT * FROM checklist_items WHERE linkedExpositionId = :expositionId")
    fun getByExposition(expositionId: Long): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items WHERE linkedTaskId = :taskId")
    fun getByTask(taskId: Long): Flow<List<ChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChecklistItemEntity): Long

    @Update
    suspend fun update(item: ChecklistItemEntity)

    @Delete
    suspend fun delete(item: ChecklistItemEntity)

    @Query("DELETE FROM checklist_items WHERE linkedExpositionId = :expositionId")
    suspend fun deleteByExposition(expositionId: Long)
}


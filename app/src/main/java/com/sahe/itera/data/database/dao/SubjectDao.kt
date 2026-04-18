package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.SubjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects WHERE isArchived = 0 ORDER BY name ASC")
    fun getSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE isArchived = 1 ORDER BY archivedAt DESC")
    fun getArchivedSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("DELETE FROM subjects WHERE isArchived = 1 AND archivedAt < :cutoff")
    suspend fun deleteExpiredArchived(cutoff: Long)
}
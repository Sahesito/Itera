package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.GradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeDao {
    @Query("SELECT * FROM grades WHERE subjectId = :subjectId ORDER BY id ASC")
    fun getBySubject(subjectId: Long): Flow<List<GradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(grade: GradeEntity)

    @Update
    suspend fun update(grade: GradeEntity)

    @Delete
    suspend fun delete(grade: GradeEntity)
}
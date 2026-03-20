package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE subjectId = :subjectId ORDER BY date DESC")
    fun getBySubject(subjectId: Long): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity)

    @Delete
    suspend fun delete(attendance: AttendanceEntity)
}
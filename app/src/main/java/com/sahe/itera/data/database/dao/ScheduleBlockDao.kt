package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.ScheduleBlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleBlockDao {
    @Query("SELECT * FROM schedule_blocks ORDER BY dayOfWeek ASC, startHour ASC")
    fun getAll(): Flow<List<ScheduleBlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(block: ScheduleBlockEntity)

    @Delete
    suspend fun delete(block: ScheduleBlockEntity)
}
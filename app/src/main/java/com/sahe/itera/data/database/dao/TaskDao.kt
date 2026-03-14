package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("""
        SELECT t.*, s.name as subjectName, s.colorHex as subjectColor
        FROM tasks t
        LEFT JOIN subjects s ON t.subjectId = s.id
        ORDER BY t.isCompleted ASC, t.dueDateTime ASC
    """)
    fun getTasks(): Flow<List<TaskWithSubject>>

    @Query("""
        SELECT t.*, s.name as subjectName, s.colorHex as subjectColor
        FROM tasks t
        LEFT JOIN subjects s ON t.subjectId = s.id
        WHERE t.subjectId = :subjectId
        ORDER BY t.isCompleted ASC, t.dueDateTime ASC
    """)
    fun getTasksBySubject(subjectId: Long): Flow<List<TaskWithSubject>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}

data class TaskWithSubject(
    val id: Long,
    val title: String,
    val description: String,
    val subjectId: Long?,
    val dueDateTime: Long?,
    val isCompleted: Boolean,
    val hasReminder: Boolean,
    val subjectName: String?,
    val subjectColor: String?,
    val priority: String = "NORMAL"
)
package com.sahe.itera.data.database.dao

import androidx.room.*
import com.sahe.itera.data.database.entities.TaskEntity
import com.sahe.itera.data.database.entities.TaskWithSubject
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("""
    SELECT t.id, t.title, t.description, t.subjectId, t.dueDateTime,
           t.isCompleted, t.hasReminder, t.priority, t.isExam,
           s.name as subjectName, s.colorHex as subjectColor
    FROM tasks t
    LEFT JOIN subjects s ON t.subjectId = s.id
    ORDER BY t.id DESC
""")
    fun getAll(): Flow<List<TaskWithSubject>>
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

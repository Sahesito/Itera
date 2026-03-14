package com.sahe.itera.data.repositories

import com.sahe.itera.data.database.dao.TaskDao
import com.sahe.itera.data.mappers.toDomain
import com.sahe.itera.data.mappers.toEntity
import com.sahe.itera.domain.model.Task
import com.sahe.itera.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> =
        dao.getTasks().map { list -> list.map { it.toDomain() } }

    override fun getTasksBySubject(subjectId: Long): Flow<List<Task>> =
        dao.getTasksBySubject(subjectId).map { list -> list.map { it.toDomain() } }

    override suspend fun getTaskById(id: Long): Task? =
        dao.getTaskById(id)?.let {
            Task(id = it.id, title = it.title, description = it.description,
                subjectId = it.subjectId, isCompleted = it.isCompleted, hasReminder = it.hasReminder)
        }

    override suspend fun insertTask(task: Task): Long =
        dao.insertTask(task.toEntity())

    override suspend fun updateTask(task: Task) =
        dao.updateTask(task.toEntity())

    override suspend fun deleteTask(task: Task) =
        dao.deleteTask(task.toEntity())
}
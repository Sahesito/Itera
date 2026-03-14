package com.sahe.itera.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahe.itera.data.database.dao.GradeDao
import com.sahe.itera.data.database.dao.SubjectDao
import com.sahe.itera.data.database.dao.TaskDao
import com.sahe.itera.data.database.entities.GradeEntity
import com.sahe.itera.data.database.entities.SubjectEntity
import com.sahe.itera.data.database.entities.TaskEntity

@Database(
    entities = [SubjectEntity::class, TaskEntity::class, GradeEntity::class],
    version = 6,
    exportSchema = false
)
abstract class IteraDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun gradeDao(): GradeDao
}



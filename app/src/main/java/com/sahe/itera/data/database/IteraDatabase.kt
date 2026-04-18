package com.sahe.itera.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahe.itera.data.database.dao.*
import com.sahe.itera.data.database.entities.*

@Database(
    entities = [
        SubjectEntity::class,
        TaskEntity::class,
        GradeEntity::class,
        ScheduleBlockEntity::class,
        AttendanceEntity::class,
        ChecklistItemEntity::class,
        ExpositionEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class IteraDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun gradeDao(): GradeDao
    abstract fun scheduleBlockDao(): ScheduleBlockDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun checklistItemDao(): ChecklistItemDao
    abstract fun expositionDao(): ExpositionDao
}



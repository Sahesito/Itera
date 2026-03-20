package com.sahe.itera.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahe.itera.data.database.dao.AttendanceDao
import com.sahe.itera.data.database.dao.GradeDao
import com.sahe.itera.data.database.dao.ScheduleBlockDao
import com.sahe.itera.data.database.dao.SubjectDao
import com.sahe.itera.data.database.dao.TaskDao
import com.sahe.itera.data.database.entities.AttendanceEntity
import com.sahe.itera.data.database.entities.GradeEntity
import com.sahe.itera.data.database.entities.ScheduleBlockEntity
import com.sahe.itera.data.database.entities.SubjectEntity
import com.sahe.itera.data.database.entities.TaskEntity

@Database(
    entities = [
        SubjectEntity::class,
        TaskEntity::class,
        GradeEntity::class,
        ScheduleBlockEntity::class,
        AttendanceEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class IteraDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun gradeDao(): GradeDao
    abstract fun scheduleBlockDao(): ScheduleBlockDao
    abstract fun attendanceDao(): AttendanceDao
}



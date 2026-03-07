package com.sahe.itera.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sahe.itera.data.database.dao.SubjectDao
import com.sahe.itera.data.database.entities.SubjectEntity

@Database(
    entities = [SubjectEntity::class],
    version = 1,
    exportSchema = false
)
abstract class IteraDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
}
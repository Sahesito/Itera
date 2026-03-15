package com.sahe.itera.di

import android.content.Context
import androidx.room.Room
import com.sahe.itera.data.database.IteraDatabase
import com.sahe.itera.data.database.dao.GradeDao
import com.sahe.itera.data.database.dao.ScheduleBlockDao
import com.sahe.itera.data.database.dao.SubjectDao
import com.sahe.itera.data.database.dao.TaskDao
import com.sahe.itera.domain.repository.GradeRepository
import com.sahe.itera.data.repositories.GradeRepositoryImpl
import com.sahe.itera.data.repositories.ScheduleRepositoryImpl
import com.sahe.itera.data.repositories.SubjectRepositoryImpl
import com.sahe.itera.data.repositories.TaskRepositoryImpl
import com.sahe.itera.domain.repository.ScheduleRepository
import com.sahe.itera.domain.repository.SubjectRepository
import com.sahe.itera.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): IteraDatabase =
        Room.databaseBuilder(
            context,
            IteraDatabase::class.java,
            "itera_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideSubjectDao(db: IteraDatabase): SubjectDao = db.subjectDao()


    @Provides
    fun provideTaskDao(db: IteraDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideGradeDao(db: IteraDatabase): GradeDao = db.gradeDao()
    @Provides
    fun provideScheduleBlockDao(db: IteraDatabase): ScheduleBlockDao = db.scheduleBlockDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindGradeRepository(impl: GradeRepositoryImpl): GradeRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository
}



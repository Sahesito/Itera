package com.sahe.itera.di

import com.sahe.itera.data.repositories.*
import com.sahe.itera.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository
    @Binds @Singleton abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
    @Binds @Singleton abstract fun bindGradeRepository(impl: GradeRepositoryImpl): GradeRepository
    @Binds @Singleton abstract fun bindScheduleRepository(impl: ScheduleRepositoryImpl): ScheduleRepository
    @Binds @Singleton abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository
    @Binds @Singleton abstract fun bindChecklistRepository(impl: ChecklistRepositoryImpl): ChecklistRepository
    @Binds @Singleton abstract fun bindExpositionRepository(impl: ExpositionRepositoryImpl): ExpositionRepository
}
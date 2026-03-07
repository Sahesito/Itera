package com.sahe.itera.di

import android.content.Context
import androidx.room.Room
import com.sahe.itera.data.database.IteraDatabase
import com.sahe.itera.data.database.dao.SubjectDao
import com.sahe.itera.data.repositories.SubjectRepositoryImpl
import com.sahe.itera.domain.repository.SubjectRepository
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
        ).build()

    @Provides
    fun provideSubjectDao(db: IteraDatabase): SubjectDao = db.subjectDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSubjectRepository(
        impl: SubjectRepositoryImpl
    ): SubjectRepository
}
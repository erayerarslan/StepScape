package com.erayerarslan.stepscape.di

import android.content.Context
import androidx.room.Room
import com.erayerarslan.stepscape.data.health.HealthConnectManager
import com.erayerarslan.stepscape.data.local.StepScapeDatabase
import com.erayerarslan.stepscape.data.local.dao.StepLogDao
import com.erayerarslan.stepscape.repository.StepLogRepository
import com.erayerarslan.stepscape.repository.StepLogRepositoryImpl
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
    fun provideDatabase(@ApplicationContext context: Context): StepScapeDatabase {
        return Room.databaseBuilder(
            context,
            StepScapeDatabase::class.java,
            StepScapeDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideStepLogDao(database: StepScapeDatabase): StepLogDao {
        return database.stepLogDao()
    }
    
    @Provides
    @Singleton
    fun provideStepLogRepository(stepLogDao: StepLogDao): StepLogRepository {
        return StepLogRepositoryImpl(stepLogDao)
    }
    
    @Provides
    @Singleton
    fun provideHealthConnectManager(@ApplicationContext context: Context): HealthConnectManager {
        return HealthConnectManager(context)
    }
}


package com.rakshakavach.di

import android.content.Context
import androidx.room.Room
import com.rakshakavach.data.local.ChecklistHistoryDao
import com.rakshakavach.data.local.IncidentDao
import com.rakshakavach.data.local.RakshaKavachDatabase
import com.rakshakavach.data.local.SafetyScoreDao
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
    fun provideRakshaKavachDatabase(
        @ApplicationContext context: Context
    ): RakshaKavachDatabase {
        return Room.databaseBuilder(
            context,
            RakshaKavachDatabase::class.java,
            "rakshakavach_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideIncidentDao(database: RakshaKavachDatabase): IncidentDao {
        return database.incidentDao
    }

    @Provides
    @Singleton
    fun provideSafetyScoreDao(database: RakshaKavachDatabase): SafetyScoreDao {
        return database.safetyScoreDao
    }

    @Provides
    @Singleton
    fun provideChecklistHistoryDao(database: RakshaKavachDatabase): ChecklistHistoryDao {
        return database.checklistHistoryDao
    }
}

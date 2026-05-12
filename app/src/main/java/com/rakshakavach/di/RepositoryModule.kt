package com.rakshakavach.di

import com.rakshakavach.data.remote.GeminiApiService
import com.rakshakavach.data.repository.GeminiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGeminiRepository(apiService: GeminiApiService): GeminiRepository {
        return GeminiRepository(apiService)
    }
    
    // Note: IncidentRepository and SafetyScoreRepository are provided automatically 
    // via their @Inject constructors, so we don't need to explicitly provide them here.
}

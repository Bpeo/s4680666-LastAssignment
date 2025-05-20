// app/src/main/java/com/example/assignmentlast/di/AppModule.kt
package com.example.assignmentlast.di

import com.example.assignmentlast.data.api.ApiService
import com.example.assignmentlast.data.repository.AppRepository
import com.example.assignmentlast.data.repository.AppRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Hilt module for dependency injection
@Module
@InstallIn(SingletonComponent::class)  // Available throughout app lifecycle
object AppModule {

    // Provides the base URL for the API
    @Provides
    @Singleton
    fun provideBaseUrl(): String = "https://nit3213api.onrender.com/"

    // Provides a configured Retrofit instance
    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provides the API service implementation
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    // Provides the repository implementation
    @Provides
    @Singleton
    fun provideAppRepository(apiService: ApiService): AppRepository {
        return AppRepositoryImpl(apiService)
    }
}
// app/src/main/java/com/example/assignmentlast/data/repository/AppRepository.kt
package com.example.assignmentlast.data.repository

import com.example.assignmentlast.data.api.ApiService
import com.example.assignmentlast.data.models.Entity
import com.example.assignmentlast.data.models.LoginRequest

import javax.inject.Inject

// Repository interface defining data operations
interface AppRepository {
    suspend fun login(username: String, password: String, location: String): Result<String>
    suspend fun getDashboard(keypass: String): Result<List<Entity>>
}

// Implementation of the repository interface
class AppRepositoryImpl @Inject constructor(
    private val apiService: ApiService  // Injected API service
) : AppRepository {

    // Login implementation - handles API call and response processing
    override suspend fun login(username: String, password: String, location: String): Result<String> {
        return try {
            val response = apiService.login(location, LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                // Return success with keypass if login successful
                Result.success(response.body()!!.keypass)
            } else {
                // Return failure with error message if login failed
                Result.failure(Exception("Login failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            // Handle network or other exceptions
            Result.failure(e)
        }
    }

    // Dashboard data retrieval implementation
    override suspend fun getDashboard(keypass: String): Result<List<Entity>> {
        return try {
            val response = apiService.getDashboard(keypass)
            if (response.isSuccessful && response.body() != null) {
                // Return success with entities if API call successful
                Result.success(response.body()!!.entities)
            } else {
                // Return failure with error message if API call failed
                Result.failure(Exception("Failed to get dashboard: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            // Handle network or other exceptions
            Result.failure(e)
        }
    }
}
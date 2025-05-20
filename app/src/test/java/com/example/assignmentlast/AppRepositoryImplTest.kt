package com.example.assignmentlast

import com.example.assignmentlast.data.api.ApiService
import com.example.assignmentlast.data.models.DashboardResponse
import com.example.assignmentlast.data.models.LoginRequest
import com.example.assignmentlast.data.models.LoginResponse
import com.example.assignmentlast.data.repository.AppRepositoryImpl
import okhttp3.ResponseBody
import retrofit2.Response
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)  // Use Mockito for mocking
class AppRepositoryImplTest {

    @Mock
    private lateinit var apiService: ApiService  // Mock API service

    private lateinit var repository: AppRepositoryImpl

    @Before
    fun setup() {
        repository = AppRepositoryImpl(apiService)  // Initialize repository with mock service
    }

    @Test
    fun `login success should return keypass`() = runTest {
        // Given - Set up test data and mock behavior
        val loginRequest = LoginRequest("username", "password")
        val loginResponse = LoginResponse("test-keypass")
        `when`(apiService.login("location", loginRequest))
            .thenReturn(Response.success(loginResponse))

        // When - Call the method being tested
        val result = repository.login("username", "password", "location")

        // Then - Verify the expected outcome
        assert(result.isSuccess)
        assert(result.getOrNull() == "test-keypass")
    }

    @Test
    fun `login failure should return error`() = runTest {
        // Given - Set up test data and mock behavior for failure case
        val loginRequest = LoginRequest("username", "password")
        `when`(apiService.login("location", loginRequest))
            .thenReturn(Response.error(401, ResponseBody.create(null, "Unauthorized")))

        // When - Call the method being tested
        val result = repository.login("username", "password", "location")

        // Then - Verify the expected outcome
        assert(result.isFailure)
    }

    @Test
    fun `getDashboard success should return entities`() = runTest {
        // Given - Set up test data and mock behavior
        val entities = listOf(
            mapOf("name" to "Property 1", "scientificName" to "Property 2", "description" to "Description 1"),
            mapOf("name" to "Property 3", "scientificName" to "Property 4", "description" to "Description 2")
        )
        val dashboardResponse = DashboardResponse(entities, 2)
        `when`(apiService.getDashboard("keypass"))
            .thenReturn(Response.success(dashboardResponse))

        // When - Call the method being tested
        val result = repository.getDashboard("keypass")

        // Then - Verify the expected outcome
        assert(result.isSuccess)
        assert(result.getOrNull()?.size == 2)
    }

    @Test
    fun `getDashboard failure should return error`() = runTest {
        // Given - Set up test data and mock behavior for failure case
        `when`(apiService.getDashboard("keypass"))
            .thenReturn(Response.error(500, ResponseBody.create(null, "Server error")))

        // When - Call the method being tested
        val result = repository.getDashboard("keypass")

        // Then - Verify the expected outcome
        assert(result.isFailure)
    }
}
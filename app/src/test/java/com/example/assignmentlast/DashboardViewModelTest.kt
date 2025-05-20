package com.example.assignmentlast

import com.example.assignmentlast.data.repository.AppRepository
import com.example.assignmentlast.ui.dashboard.DashboardViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)  // Use Mockito for mocking
class DashboardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()  // Rule for testing LiveData

    private val testDispatcher = StandardTestDispatcher()  // Test dispatcher for coroutines

    @Mock
    private lateinit var repository: AppRepository  // Mock repository

    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)  // Set test dispatcher for main thread
        viewModel = DashboardViewModel(repository)  // Initialize ViewModel with mock repository
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()  // Reset main dispatcher after tests
    }

    @Test
    fun `fetchDashboard success should update live data`() = runTest {
        // Given - Set up test data and mock behavior
        val entities = listOf(
            mapOf("name" to "Property 1", "scientificName" to "Property 2", "description" to "Description 1"),
            mapOf("name" to "Property 3", "scientificName" to "Property 4", "description" to "Description 2")
        )
        `when`(repository.getDashboard("keypass"))
            .thenReturn(Result.success(entities))

        // When - Call the method being tested
        viewModel.fetchDashboard("keypass")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Then - Verify the expected outcome
        val result = viewModel.entities.value
        assert(result != null)
        assert(result!!.isSuccess)
        assert(result.getOrNull()?.size == 2)
    }

    @Test
    fun `fetchDashboard failure should update live data with error`() = runTest {
        // Given - Set up test data and mock behavior for failure case
        val exception = Exception("Network error")
        `when`(repository.getDashboard("keypass"))
            .thenReturn(Result.failure(exception))

        // When - Call the method being tested
        viewModel.fetchDashboard("keypass")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Then - Verify the expected outcome
        val result = viewModel.entities.value
        assert(result != null)
        assert(result!!.isFailure)
        assert(result.exceptionOrNull()?.message == "Network error")
    }
}
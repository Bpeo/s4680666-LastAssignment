package com.example.assignmentlast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.assignmentlast.data.repository.AppRepository
import com.example.assignmentlast.ui.login.LoginViewModel
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
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: AppRepository

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login success should update live data`() = runTest {
        // Given
        val keypass = "testKeypass"
        `when`(repository.login("username", "password", "location"))
            .thenReturn(Result.success(keypass))

        // When
        viewModel.login("username", "password", "location")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Then
        val result = viewModel.loginResult.value
        assert(result != null)
        assert(result!!.isSuccess)
        assert(result.getOrNull() == keypass)

        // Verify loading state changes
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun `login failure should update live data with error`() = runTest {
        // Given
        val exception = Exception("Login failed")
        `when`(repository.login("username", "password", "location"))
            .thenReturn(Result.failure(exception))

        // When
        viewModel.login("username", "password", "location")
        testDispatcher.scheduler.advanceUntilIdle() // Wait for coroutines to complete

        // Then
        val result = viewModel.loginResult.value
        assert(result != null)
        assert(result!!.isFailure)
        assert(result.exceptionOrNull()?.message == "Login failed")

        // Verify loading state changes
        assert(viewModel.isLoading.value == false)
    }
}

// app/src/main/java/com/example/assignmentlast/ui/login/LoginViewModel.kt
package com.example.assignmentlast.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentlast.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel  // Hilt annotation for ViewModel injection
class LoginViewModel @Inject constructor(
    private val repository: AppRepository  // Injected repository
) : ViewModel() {

    // LiveData for login result with success/failure handling
    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Function to perform login through repository
    fun login(username: String, password: String, location: String) {
        viewModelScope.launch {
            _isLoading.value = true  // Show loading indicator
            _loginResult.value = repository.login(username, password, location)  // Attempt login
            _isLoading.value = false  // Hide loading indicator
        }
    }
}
// app/src/main/java/com/example/assignmentlast/ui/dashboard/DashboardViewModel.kt
package com.example.assignmentlast.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignmentlast.data.models.Entity
import com.example.assignmentlast.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

// Hilt annotation for ViewModel injection
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AppRepository  // Injected repository
) : ViewModel() {

    // LiveData for entity list with success/failure handling
    private val _entities = MutableLiveData<Result<List<Entity>>>()
    val entities: LiveData<Result<List<Entity>>> = _entities

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Function to fetch dashboard data from repository
    fun fetchDashboard(keypass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _entities.value = repository.getDashboard(keypass)
            _isLoading.value = false
        }
    }
}
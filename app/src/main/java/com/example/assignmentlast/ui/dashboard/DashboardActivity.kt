package com.example.assignmentlast.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignmentlast.databinding.ActivityDashboardBinding
import com.example.assignmentlast.ui.details.DetailsActivity
import com.example.assignmentlast.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

// Enables Hilt dependency injection for this activity
@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()  // Inject ViewModel
    private lateinit var adapter: EntityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()  // Initialize RecyclerView and adapter
        setupObservers()     // Set up LiveData observers
        setupLogoutButton()  // Configure logout functionality

        // Get keypass from intent and validate
        val keypass = intent.getStringExtra("keypass") ?: ""
        if (keypass.isBlank()) {
            Toast.makeText(this, "Invalid keypass", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        // Fetch dashboard data using the keypass
        viewModel.fetchDashboard(keypass)
    }

    // Initialize RecyclerView with adapter and click listener
    private fun setupRecyclerView() {
        adapter = EntityAdapter { entity ->
            // Navigate to details screen when an entity is clicked
            val intent = Intent(this, DetailsActivity::class.java).apply {
                @Suppress("UNCHECKED_CAST")
                putExtra("entity", HashMap(entity) as HashMap<String, Any>)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // Set up observers for LiveData from ViewModel
    private fun setupObservers() {
        // Observe entities data
        viewModel.entities.observe(this) { result ->
            result.fold(
                onSuccess = { entities ->
                    adapter.submitList(entities)
                    // Show/hide empty view based on data availability
                    binding.emptyView.visibility = if (entities.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerView.visibility = if (entities.isEmpty()) View.GONE else View.VISIBLE
                },
                onFailure = { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    binding.emptyView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            )
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    // Set up logout button click listener
    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            navigateToLogin()
        }
    }

    // Navigate back to login screen
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}

package com.example.assignmentlast.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.assignmentlast.R
import com.example.assignmentlast.databinding.ActivityLoginBinding
import com.example.assignmentlast.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint  // Enable Hilt dependency injection
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()  // Inject ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()       // ⬅️ Add this line to apply blue to status bar
        setupLocationSpinner()
        setupObservers()
        setupClickListeners()
    }

    // ⬇️ NEW: Set blue color to the top status bar
    private fun setStatusBarColor() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val controller = window.insetsController
            controller?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0  // light text/icons for dark background
        }
    }

    private fun setupLocationSpinner() {
        val locations = arrayOf("Sydney", "Melbourne", "Perth")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, locations)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.locationSpinner.adapter = adapter
        binding.locationSpinner.setSelection(0)
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            result.fold(
                onSuccess = { keypass ->
                    val intent = Intent(this, DashboardActivity::class.java).apply {
                        putExtra("keypass", keypass)
                    }
                    startActivity(intent)
                },
                onFailure = { error ->
                    Toast.makeText(this, "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loginButton.isEnabled = !isLoading
        }
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val location = binding.locationSpinner.selectedItem.toString().lowercase()
            viewModel.login(username, password, location)
        }
    }
}


package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import com.example.kachingaapp_prog7313poe.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = UserRepository(
        KachingaDatabase.getDatabase(application).userDao()
    )
    private val session = SessionManager(application)

    val isLoggedIn: StateFlow<Boolean> = session.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    private val _navigateAfterRegister = MutableStateFlow(false)
    val navigateAfterRegister: StateFlow<Boolean> = _navigateAfterRegister.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(error = "Email is required") }
            return
        }
        if (password.isBlank()) {
            _uiState.update { it.copy(error = "Password is required") }
            return
        }
        if (_uiState.value.isLoading) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val result = repo.login(email.trim(), password.trim())
                result.fold(
                    onSuccess = { user ->
                        Log.d("AuthViewModel", "Firebase Login success for: ${user.email}")
                        // Make sure your SessionManager accepts user.id as a String now!
                        session.saveSession(user.id, user.fullName, user.email)
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        _navigateToHome.update { true }
                    },
                    onFailure = { e ->
                        Log.e("AuthViewModel", "Login failed: ${e.message}")
                        _uiState.update {
                            it.copy(isLoading = false, error = e.message ?: "Authentication failed")
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login exception: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Login failed. Please try again.")
                }
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        when {
            fullName.isBlank() -> {
                _uiState.update { it.copy(error = "Full name is required") }
                return
            }
            email.isBlank() || !email.contains("@") -> {
                _uiState.update { it.copy(error = "Valid email is required") }
                return
            }
            password.length < 6 -> {
                _uiState.update { it.copy(error = "Password must be at least 6 characters") }
                return
            }
            password != confirmPassword -> {
                _uiState.update { it.copy(error = "Passwords do not match") }
                return
            }
        }
        if (_uiState.value.isLoading) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Pass plaintext values down to Firebase Auth
                val result = repo.register(
                    fullName = fullName.trim(),
                    email = email.trim(),
                    password = password.trim()
                )
                result.fold(
                    onSuccess = { user ->
                        session.saveSession(user.id, user.fullName, user.email)
                        _uiState.update { it.copy(isLoading = false, error = null) }
                        _navigateAfterRegister.update { true }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(isLoading = false, error = e.message ?: "Registration failed")
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Register exception: ${e.message}", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Registration failed. Please try again.")
                }
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repo.firebaseSignOut()
                session.clearSession()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Logout exception: ${e.message}")
            }
            viewModelScope.launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun onNavigatedToHome() { _navigateToHome.update { false } }
    fun onNavigatedAfterRegister() { _navigateAfterRegister.update { false } }
    fun clearError() { _uiState.update { it.copy(error = null) } }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)
package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import com.example.kachingaapp_prog7313poe.data.entity.User
import com.example.kachingaapp_prog7313poe.data.repository.UserRepository

class AuthViewModel(application: Application) : AndroidViewModel(application) {

     private val repo = UserRepository(
        KachingaDatabase.getDatabase(application).userDao()
    )
    private val session = SessionManager(application)

    val isLoggedIn: StateFlow<Boolean> = session.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Separate state just for navigation — avoids calling lambda from bg thread
    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome: StateFlow<Boolean> = _navigateToHome.asStateFlow()

    private val _navigateAfterRegister = MutableStateFlow(false)
    val navigateAfterRegister: StateFlow<Boolean> = _navigateAfterRegister.asStateFlow()

}
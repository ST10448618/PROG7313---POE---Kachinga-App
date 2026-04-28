package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val monthlySalary: Double = 0.0,
    val savingsTargetPct: Double = 0.0,
    val monthlyBudget: Double = 0.0,
    val minMonthlyGoal: Double = 0.0,
    val maxMonthlyGoal: Double = 0.0,
    val currency: String = "ZAR (R)"
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = KachingaDatabase.getDatabase(application).userDao()
    private val session = SessionManager(application)

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _isEditingPersonal = MutableStateFlow(false)
    val isEditingPersonal: StateFlow<Boolean> = _isEditingPersonal.asStateFlow()

    private val _isEditingFinancial = MutableStateFlow(false)
    val isEditingFinancial: StateFlow<Boolean> = _isEditingFinancial.asStateFlow()

}
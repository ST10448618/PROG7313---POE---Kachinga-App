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

}
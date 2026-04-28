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

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            combine(
                session.userId,
                session.userName,
                session.userEmail,
                session.monthlyIncome,
                session.savingsTargetPct,
                session.currency,
                session.minMonthlyGoal,
                session.maxMonthlyGoal
            ) { args ->
                val userId = args[0] as Int
                val name = args[1] as String
                val email = args[2] as String
                val income = args[3] as Double
                val savingsPct = args[4] as Double
                val currency = args[5] as String
                val minGoal = args[6] as Double
                val maxGoal = args[7] as Double

                var fullName = name
                var userEmail = email

                if (userId > 0) {
                    val user = userDao.getUserById(userId).first()
                    fullName = user?.fullName ?: name
                    userEmail = user?.email ?: email
                }

                ProfileState(
                    fullName = fullName,
                    email = userEmail,
                    monthlySalary = income,
                    savingsTargetPct = savingsPct,
                    currency = currency,
                    minMonthlyGoal = minGoal,
                    maxMonthlyGoal = maxGoal
                )
            }.collect { state ->
                _profileState.value = state
            }
        }
    }

    fun toggleEditingPersonal() { _isEditingPersonal.update { !it } }
    fun toggleEditingFinancial() { _isEditingFinancial.update { !it } }

    fun savePersonalInfo(fullName: String, email: String, phone: String) {
        viewModelScope.launch {
            val userId = session.userId.first()
            if (userId > 0) {
                val existing = userDao.getUserById(userId).first()
                if (existing != null) {
                    userDao.updateUser(
                        existing.copy(fullName = fullName.trim(), email = email.trim())
                    )
                    session.saveSession(userId, fullName.trim(), email.trim())
                }
            }
            _profileState.update { it.copy(fullName = fullName, email = email, phone = phone) }
            _isEditingPersonal.update { false }
        }
    }

    fun saveFinancialSettings(
        salary: Double,
        savingsPct: Double,
        budget: Double,
        currency: String,
        minGoal: Double,
        maxGoal: Double
    ) {
        viewModelScope.launch {
            session.saveFinancialSettings(salary, savingsPct, currency, minGoal, maxGoal)
            _profileState.update {
                it.copy(
                    monthlySalary = salary,
                    savingsTargetPct = savingsPct,
                    monthlyBudget = budget,
                    currency = currency,
                    minMonthlyGoal = minGoal,
                    maxMonthlyGoal = maxGoal
                )
            }
            _isEditingFinancial.update { false }
        }
    }

}
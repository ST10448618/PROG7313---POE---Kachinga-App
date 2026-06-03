package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import com.example.kachingaapp_prog7313poe.data.entity.SavingsGoal
import com.example.kachingaapp_prog7313poe.data.repository.SavingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SavingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SavingsRepository(
        KachingaDatabase.getDatabase(application).savingsGoalDao()
    )
    private val session = SessionManager(application)

    private val userId: StateFlow<String> = session.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val allGoals: StateFlow<List<SavingsGoal>> = userId
        .flatMapLatest { id ->
            if (id.isNotBlank()) repo.getAll(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(SavingsUiState())
    val uiState: StateFlow<SavingsUiState> = _uiState.asStateFlow()

    fun addGoal(name: String, icon: String, targetAmount: Double) {
        viewModelScope.launch {
            val currentUserId = session.userId.first()
            //Prevent insertions if unauthenticated or empty string token detected
            if (currentUserId.isBlank()) return@launch

            repo.insert(
                SavingsGoal(
                    userId = currentUserId, //Now safely passes down the String UID
                    name = name,
                    icon = icon,
                    targetAmount = targetAmount
                )
            ).fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Goal created!") }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(error = e.message ?: "Failed to create goal")
                    }
                }
            )
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch { repo.delete(goal) }
    }

    fun getGoalById(id: Int): Flow<SavingsGoal?> {
        val currentUserId = userId.value
        //Validates the token before sending transaction to database repository layer
        return if (currentUserId.isNotBlank())
            repo.getById(id, currentUserId)
        else flowOf(null)
    }

    fun deposit(goalId: Int, amount: Double) {
        viewModelScope.launch {
            val currentUserId = session.userId.first()
            //Prevent query execution if string session token validation fails
            if (currentUserId.isBlank()) return@launch

            repo.deposit(goalId, currentUserId, amount).fold(
                onSuccess = {
                    _uiState.update { it.copy(successMessage = "Deposit added!") }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message ?: "Deposit failed") }
                }
            )
        }
    }



    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }




}

data class SavingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
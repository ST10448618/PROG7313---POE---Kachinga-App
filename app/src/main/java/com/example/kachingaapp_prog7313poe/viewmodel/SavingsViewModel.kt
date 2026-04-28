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

    private val userId: StateFlow<Int> = session.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    val allGoals: StateFlow<List<SavingsGoal>> = userId
        .flatMapLatest { id ->
            if (id > 0) repo.getAll(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(SavingsUiState())
    val uiState: StateFlow<SavingsUiState> = _uiState.asStateFlow()

    fun addGoal(name: String, icon: String, targetAmount: Double) {
        viewModelScope.launch {
            val currentUserId = session.userId.first()
            if (currentUserId <= 0) return@launch
            repo.insert(
                SavingsGoal(
                    userId = currentUserId,
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
        return if (currentUserId > 0)
            repo.getById(id, currentUserId)
        else flowOf(null)
    }




}

data class SavingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
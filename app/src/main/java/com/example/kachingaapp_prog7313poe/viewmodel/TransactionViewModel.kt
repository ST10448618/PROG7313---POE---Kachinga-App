package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import com.example.kachingaapp_prog7313poe.data.entity.AppTransaction
import com.example.kachingaapp_prog7313poe.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = TransactionRepository(
        KachingaDatabase.getDatabase(application).transactionDao()
    )
    private val session = SessionManager(application)

    // React to userId changes — when user switches, data refreshes automatically
    private val userId: StateFlow<Int> = session.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    val allTransactions: StateFlow<List<AppTransaction>> = userId
        .flatMapLatest { id ->
            if (id > 0) repo.getAllTransactions(id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = userId
        .flatMapLatest { id ->
            if (id > 0) repo.getTotalIncome(id)
            else flowOf(0.0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpenses: StateFlow<Double> = userId
        .flatMapLatest { id ->
            if (id > 0) repo.getTotalExpenses(id)
            else flowOf(0.0)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val balance: StateFlow<Double> = combine(totalIncome, totalExpenses) { i, e -> i - e }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    fun addTransaction(
        title: String,
        amount: String,
        categoryId: Int,
        categoryName: String,
        categoryIcon: String,
        isExpense: Boolean,
        note: String = "",
        description: String = "",
        startTime: String = "",
        endTime: String = "",
        date: Long = System.currentTimeMillis(),
        imagePath: String = "",
        onSuccess: () -> Unit
    ) {
        val amountDouble = amount.toDoubleOrNull()
        if (amountDouble == null || amountDouble <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val currentUserId = session.userId.first()
            if (currentUserId <= 0) {
                _uiState.update { it.copy(error = "Please log in again") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, error = null) }
            repo.insert(
                AppTransaction(
                    userId = currentUserId,
                    title = title.ifBlank { categoryName },
                    description = description,
                    amount = amountDouble,
                    categoryId = categoryId,
                    categoryName = categoryName,
                    categoryIcon = categoryIcon,
                    isExpense = isExpense,
                    note = note,
                    imagePath = imagePath,
                    date = date,
                    startTime = startTime,
                    endTime = endTime
                )
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false, successMessage = "Transaction added!")
                    }
                    viewModelScope.launch(Dispatchers.Main) { onSuccess() }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed")
                    }
                }
            )
        }
    }

    fun getByDateRange(start: Long, end: Long): Flow<List<AppTransaction>> {
        val currentUserId = userId.value
        return if (currentUserId > 0)
            repo.getByDateRange(currentUserId, start, end)
        else flowOf(emptyList())
    }

    fun getExpensesByDateRange(start: Long, end: Long): Flow<List<AppTransaction>> {
        val currentUserId = userId.value
        return if (currentUserId > 0)
            repo.getExpensesByDateRange(currentUserId, start, end)
        else flowOf(emptyList())
    }
}

data class TransactionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
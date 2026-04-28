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
}
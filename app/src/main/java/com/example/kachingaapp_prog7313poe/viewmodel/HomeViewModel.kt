package com.example.kachingaapp_prog7313poe.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import com.example.kachingaapp_prog7313poe.data.entity.AppTransaction
import kotlinx.coroutines.flow.*
import java.util.Calendar


enum class TimeFilter {
    DAILY, WEEKLY, MONTHLY
}

data class HomeFinancialState(
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val monthlySalary: Double = 0.0,
    val savingsTargetPct: Double = 0.0,
    val currency: String = "ZAR (R)",
    val healthPercent: Int = 0,
    val transactions: List<AppTransaction> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val transactionDao = KachingaDatabase.getDatabase(application).transactionDao()
    private val session = SessionManager(application)
    private val selectedFilter = MutableStateFlow(TimeFilter.MONTHLY)




}
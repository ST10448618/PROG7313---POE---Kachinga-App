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

    fun setFilter(filter: TimeFilter) {
        selectedFilter.value = filter
    }


    val financialState: StateFlow<HomeFinancialState> = session.userId
        .flatMapLatest { userId ->
            if (userId.isBlank()) return@flatMapLatest flowOf(HomeFinancialState())

            combine(
                transactionDao.getAllTransactions(userId),
                session.monthlyIncome,
                session.savingsTargetPct,
                session.currency,
                selectedFilter
            ) { args ->
                @Suppress("UNCHECKED_CAST")
                val transactions = args[0] as List<AppTransaction>
                val salary      = args[1] as Double
                val savingsPct  = args[2] as Double
                val currency    = args[3] as String
                val filter      = args[4] as TimeFilter

                val now = System.currentTimeMillis()

                val filteredTransactions = when (filter) {
                    TimeFilter.DAILY -> {
                        val today = Calendar.getInstance().apply { timeInMillis = now }
                        transactions.filter {
                            val c = Calendar.getInstance().apply { timeInMillis = it.date }
                            c.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                                    c.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                        }
                    }
                    TimeFilter.WEEKLY -> {
                        val week = Calendar.getInstance().apply { timeInMillis = now }
                        transactions.filter {
                            val c = Calendar.getInstance().apply { timeInMillis = it.date }
                            c.get(Calendar.WEEK_OF_YEAR) == week.get(Calendar.WEEK_OF_YEAR) &&
                                    c.get(Calendar.YEAR) == week.get(Calendar.YEAR)
                        }
                    }
                    TimeFilter.MONTHLY -> {
                        val currentMonth = Calendar.getInstance().apply { timeInMillis = now }
                        transactions.filter {
                            val c = Calendar.getInstance().apply { timeInMillis = it.date }
                            c.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
                                    c.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR)
                        }
                    }
                }

                val filteredExpenses = filteredTransactions
                    .filter { it.isExpense }
                    .sumOf { it.amount }

                val filteredTransactionIncome = filteredTransactions
                    .filter { !it.isExpense }
                    .sumOf { it.amount }

                val effectiveIncome = salary + filteredTransactionIncome
                val balance = effectiveIncome - filteredExpenses
                val healthPct = if (salary > 0) ((filteredExpenses / salary) * 100).toInt() else 0

                HomeFinancialState(
                    balance = balance,
                    totalIncome = effectiveIncome,
                    totalExpenses = filteredExpenses,
                    monthlySalary = salary,
                    savingsTargetPct = savingsPct,
                    currency = currency,
                    healthPercent = healthPct,
                    transactions = filteredTransactions
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            HomeFinancialState()
        )


}
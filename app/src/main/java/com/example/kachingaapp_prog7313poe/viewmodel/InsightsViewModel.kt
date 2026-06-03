package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import kotlin.compareTo
import kotlin.div
import kotlin.text.lowercase
import kotlin.text.toInt
import kotlin.times


data class CategorySpending(
    val category: String,
    val currentMonth: Double,
    val lastMonth: Double,
    val percentChange: Float,
    val trend: String // "UP", "DOWN", "STABLE"
)

data class SpendingInsight(
    val title: String,
    val description: String,
    val icon: String,
    val actionable: Boolean = false,
    val potentialSavings: Double = 0.0
)

data class InsightState(
    val categoryChanges: List<CategorySpending> = emptyList(),
    val insights: List<SpendingInsight> = emptyList(),
    val savingsGoalProgress: Int = 0,
    val onTrack: Boolean = true,
    val isLoading: Boolean = false
)

class InsightsViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao = KachingaDatabase.getDatabase(application).transactionDao()
    private val session = SessionManager(application)

    private val _insightState = MutableStateFlow(InsightState())
    val insightState: StateFlow<com.example.prog7313_poe_kachinga.viewmodel.InsightState> =
        _insightState.asStateFlow()

    init {
        viewModelScope.launch {
            // React to userId, salary, savingsPct, AND transactions all at once
            session.userId.collect { userId ->
                if (userId > 0) {
                    // Combine transactions + salary + savingsPct into one stream
                    combine(
                        transactionDao.getAllTransactions(userId),
                        session.monthlyIncome,
                        session.savingsTargetPct
                    ) { transactions, income, savingsPct ->
                        Triple(transactions, income, savingsPct)
                    }.collectLatest { (_, _, _) ->
                        generateInsights(userId)
                    }
                }
            }
        }
    }

    private fun generateInsights(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _insightState.update { it.copy(isLoading = true) }
            try {
                val now = Calendar.getInstance()
                val currentMonth = now.get(Calendar.MONTH)
                val currentYear = now.get(Calendar.YEAR)

                val currentMonthStart = Calendar.getInstance().apply {
                    set(currentYear, currentMonth, 1, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val currentMonthEnd = Calendar.getInstance().apply {
                    set(
                        currentYear, currentMonth,
                        getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59
                    )
                }.timeInMillis

                val lastMonthStart = Calendar.getInstance().apply {
                    if (currentMonth == 0) {
                        set(currentYear - 1, 11, 1, 0, 0, 0)
                    } else {
                        set(currentYear, currentMonth - 1, 1, 0, 0, 0)
                    }
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val lastMonthEnd = Calendar.getInstance().apply {
                    if (currentMonth == 0) {
                        set(
                            currentYear - 1, 11,
                            getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59
                        )
                    } else {
                        set(
                            currentYear, currentMonth - 1,
                            getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59
                        )
                    }
                }.timeInMillis

                val currentMonthExpenses =
                    transactionDao.getExpensesByDateRange(
                        userId,
                        currentMonthStart,
                        currentMonthEnd
                    ).first()
                val lastMonthExpenses =
                    transactionDao.getExpensesByDateRange(userId, lastMonthStart, lastMonthEnd)
                        .first()
                val currentMonthIncome =
                    transactionDao.getIncomeByDateRange(userId, currentMonthStart, currentMonthEnd)
                        .first()

                val salary = session.monthlyIncome.first()
                val savingsTarget = session.savingsTargetPct.first()

                val categoryChanges =
                    calculateCategoryChanges(currentMonthExpenses, lastMonthExpenses)

                val insights =
                    mutableListOf<com.example.prog7313_poe_kachinga.viewmodel.SpendingInsight>()

                // Top spending increase
                categoryChanges.maxByOrNull { it.percentChange }?.let { top ->
                    if (top.percentChange > 0) {
                        insights.add(
                            SpendingInsight(
                                title = "${top.category} Spending Up",
                                description = "${top.category} spending increased by ${top.percentChange.toInt()}% this month.",
                                icon = "📈",
                                actionable = top.percentChange > 20
                            )
                        )
                    }
                }
                // Savings opportunity
                categoryChanges.maxByOrNull { it.currentMonth }?.let { high ->
                    val potentialSavings = high.currentMonth * 0.10
                    if (potentialSavings > 100) {
                        insights.add(
                            SpendingInsight(
                                title = "Savings Opportunity",
                                description = "Reducing ${high.category.lowercase()} by 10% could save R${"%.0f".format(potentialSavings)}/month.",
                                icon = "💰",
                                actionable = true,
                                potentialSavings = potentialSavings
                            )
                        )
                    }
                }

                // Use actual income from transactions if no salary set
                val effectiveIncome = if (salary > 0) salary
                else currentMonthIncome.sumOf { it.amount }

                val totalExpenses = currentMonthExpenses.sumOf { it.amount }
                val budgetPercentage = if (effectiveIncome > 0)
                    (totalExpenses / effectiveIncome * 100).toInt()
                else 0
                val onTrack = budgetPercentage < 80

                insights.add(
                    SpendingInsight(
                        title = if (onTrack) "On Budget" else "Overspending Alert",
                        description = if (onTrack)
                            "You're spending $budgetPercentage% of your income. Great job!"
                        else
                            "You're spending $budgetPercentage% of your income. Consider cutting back.",
                        icon = if (onTrack) "✅" else "⚠️",
                        actionable = !onTrack
                    )
                )

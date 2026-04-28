package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import com.example.kachingaapp_prog7313poe.data.entity.Achievement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AchievementsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = KachingaDatabase.getDatabase(application).achievementDao()
    private val transactionDao = KachingaDatabase.getDatabase(application).transactionDao()
    private val savingsDao = KachingaDatabase.getDatabase(application).savingsGoalDao()
    private val categoryDao = KachingaDatabase.getDatabase(application).categoryDao()
    private val session = SessionManager(application)



    fun checkAndAwardAchievements() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = session.userId.first()
                if (userId <= 0) return@launch

                val transactions = transactionDao.getAllTransactions(userId).first()
                val goals = savingsDao.getAllGoals(userId).first()
                val categories = categoryDao.getAllCategories(userId).first()
                val salary = session.monthlyIncome.first()
                val savingsPct = session.savingsTargetPct.first()

                if (transactions.isNotEmpty()) dao.awardAchievement("First Saver")

                val totalIncome = transactions.filter { !it.isExpense }.sumOf { it.amount }
                if (totalIncome >= 2000.0) dao.awardAchievement("Big Spender")
                if (totalIncome >= 10000.0) dao.awardAchievement("High Earner")

                if (goals.any { it.targetAmount > 0 && it.savedAmount >= it.targetAmount })
                    dao.awardAchievement("Goal Crusher")

                if (goals.any { it.targetAmount > 0 && (it.savedAmount / it.targetAmount) >= 0.5 })
                    dao.awardAchievement("Half Way There")

                if (goals.size >= 3) dao.awardAchievement("Multi Goal")

                val distinctDays = transactions.map { tx ->
                    val cal = java.util.Calendar.getInstance()
                    cal.timeInMillis = tx.date
                    "${cal.get(java.util.Calendar.YEAR)}-${cal.get(java.util.Calendar.DAY_OF_YEAR)}"
                }.distinct()
                if (distinctDays.size >= 7) dao.awardAchievement("7 Day Streak")
                if (distinctDays.size >= 30) dao.awardAchievement("Champion")

                if (transactions.count { !it.isExpense } >= 5)
                    dao.awardAchievement("Consistent Saver")
                if (transactions.count { it.isExpense } >= 5)
                    dao.awardAchievement("Frugal Five")
                if (transactions.any { it.imagePath.isNotBlank() })
                    dao.awardAchievement("Receipt Keeper")

                if (salary > 0 && savingsPct > 0) dao.awardAchievement("Profile Pro")

                // Custom categories = those with userId > 0
                if (categories.count { it.userId > 0 } >= 1)
                    dao.awardAchievement("Category Creator")

                if (salary > 0) {
                    val cal = java.util.Calendar.getInstance()
                    val month = cal.get(java.util.Calendar.MONTH)
                    val year = cal.get(java.util.Calendar.YEAR)
                    val thisMonthExpenses = transactions.filter { tx ->
                        val txCal = java.util.Calendar.getInstance()
                        txCal.timeInMillis = tx.date
                        tx.isExpense &&
                                txCal.get(java.util.Calendar.MONTH) == month &&
                                txCal.get(java.util.Calendar.YEAR) == year
                    }.sumOf { it.amount }
                    if (thisMonthExpenses in 1.0..salary)
                        dao.awardAchievement("Budget Master")
                }

            } catch (e: Exception) {
                Log.e("AchievementsViewModel", "Error: ${e.message}")
            }
        }
    }

}
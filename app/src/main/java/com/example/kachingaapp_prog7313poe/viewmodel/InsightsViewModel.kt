package com.example.kachingaapp_prog7313poe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kachingaapp_prog7313poe.data.KachingaDatabase
import com.example.kachingaapp_prog7313poe.data.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


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

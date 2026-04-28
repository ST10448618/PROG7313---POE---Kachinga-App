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

}
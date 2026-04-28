package com.example.kachingaapp_prog7313poe.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "kachinga_session")

class SessionManager(private val context: Context) {

    companion object {
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val MONTHLY_INCOME = doublePreferencesKey("monthly_income")
        val SAVINGS_TARGET_PCT = doublePreferencesKey("savings_target_pct")
        val CURRENCY = stringPreferencesKey("currency")
        val MIN_MONTHLY_GOAL = doublePreferencesKey("min_monthly_goal")
        val MAX_MONTHLY_GOAL = doublePreferencesKey("max_monthly_goal")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.IS_LOGGED_IN] ?: false }

    val userId: Flow<Int> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.USER_ID] ?: -1 }

    val userName: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.USER_NAME] ?: "" }

    val userEmail: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.USER_EMAIL] ?: "" }

    val monthlyIncome: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.MONTHLY_INCOME] ?: 0.0 }

    val savingsTargetPct: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.SAVINGS_TARGET_PCT] ?: 20.0 }

    val currency: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.CURRENCY] ?: "ZAR (R)" }

    val minMonthlyGoal: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.MIN_MONTHLY_GOAL] ?: 0.0 }

    val maxMonthlyGoal: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[com.example.kachingaapp_prog7313poe.data.SessionManager.Companion.MAX_MONTHLY_GOAL] ?: 0.0 }
}
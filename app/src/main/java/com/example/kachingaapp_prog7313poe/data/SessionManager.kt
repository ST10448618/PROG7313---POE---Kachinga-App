package com.example.kachingaapp_prog7313poe.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "kachinga_session")

class SessionManager(private val context: Context) {

    //Values Added
    companion object {
        //Shifted from intPreferencesKey to stringPreferencesKey to accommodate Firebase UIDs
        val USER_ID = stringPreferencesKey("user_id")
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
        .map {
            it[IS_LOGGED_IN]
                ?: false
        }

    //Flow type changed from Int to String, falling back to empty string instead of -1
    val userId: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { it[USER_ID] ?: "" }

    val userName: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[USER_NAME] ?: ""
        }

    val userEmail: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[USER_EMAIL] ?: ""
        }

    val monthlyIncome: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[MONTHLY_INCOME]
                ?: 0.0
        }

    val savingsTargetPct: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[SAVINGS_TARGET_PCT]
                ?: 20.0
        }

    val currency: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[CURRENCY]
                ?: "ZAR (R)"
        }

    val minMonthlyGoal: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[MIN_MONTHLY_GOAL]
                ?: 0.0
        }

    val maxMonthlyGoal: Flow<Double> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map {
            it[MAX_MONTHLY_GOAL]
                ?: 0.0
        }

    // Save the Session
    //Parameter userId converted from Int to String
    suspend fun saveSession(userId: String, userName: String, userEmail: String) {
        try {
            context.dataStore.edit { prefs ->
                prefs[USER_ID] =
                    userId
                prefs[USER_NAME] =
                    userName
                prefs[USER_EMAIL] =
                    userEmail
                prefs[IS_LOGGED_IN] =
                    true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Save Financial Settings
    suspend fun saveFinancialSettings(
        income: Double,
        savingsPct: Double,
        currency: String,
        minGoal: Double,
        maxGoal: Double
    ) {
        try {
            context.dataStore.edit { prefs ->
                prefs[MONTHLY_INCOME] = income
                prefs[SAVINGS_TARGET_PCT] = savingsPct
                prefs[CURRENCY] = currency
                prefs[MIN_MONTHLY_GOAL] = minGoal
                prefs[MAX_MONTHLY_GOAL] = maxGoal
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    //Clear the Session
    suspend fun clearSession() {
        try {
            context.dataStore.edit { it.clear() }
        } catch (e: Exception) { e.printStackTrace() }
    }
}
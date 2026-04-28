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
}
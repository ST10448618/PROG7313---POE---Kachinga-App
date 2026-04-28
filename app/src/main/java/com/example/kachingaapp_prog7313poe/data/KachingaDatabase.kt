package com.example.kachingaapp_prog7313poe.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kachingaapp_prog7313poe.data.dao.*
import com.example.kachingaapp_prog7313poe.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        AppTransaction::class,
        Category::class,
        SavingsGoal::class,
        Achievement::class
    ],
    version = 4,
    exportSchema = false
)
abstract class KachingaDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun achievementDao(): AchievementDao
}
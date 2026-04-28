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

    companion object {
        @Volatile
        private var INSTANCE: com.example.kachingaapp_prog7313poe.data.KachingaDatabase? = null

        fun getDatabase(context: Context): com.example.kachingaapp_prog7313poe.data.KachingaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    com.example.kachingaapp_prog7313poe.data.KachingaDatabase::class.java,
                    "kachinga_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let {
                                    seedCategories(it.categoryDao())
                                    seedAchievements(it.achievementDao())
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private suspend fun seedCategories(dao: CategoryDao) {
            // userId = 0 means shared/default — visible to all users
            listOf(
                Category(userId = 0, name = "Food",          icon = "🍔", isExpense = true),
                Category(userId = 0, name = "Transport",     icon = "🚌", isExpense = true),
                Category(userId = 0, name = "Medicine",      icon = "💊", isExpense = true),
                Category(userId = 0, name = "Travel",        icon = "✈",  isExpense = true),
                Category(userId = 0, name = "Property",      icon = "🏠", isExpense = true),
                Category(userId = 0, name = "Car",           icon = "🚗", isExpense = true),
                Category(userId = 0, name = "Grocery",       icon = "🛒", isExpense = true),
                Category(userId = 0, name = "Rent",          icon = "🏢", isExpense = true),
                Category(userId = 0, name = "Gifts",         icon = "🎁", isExpense = true),
                Category(userId = 0, name = "Entertainment", icon = "🎬", isExpense = true),
                Category(userId = 0, name = "Salary",        icon = "💵", isExpense = false),
                Category(userId = 0, name = "Freelance",     icon = "💻", isExpense = false),
                Category(userId = 0, name = "Investment",    icon = "📈", isExpense = false),
                Category(userId = 0, name = "Other Income",  icon = "💰", isExpense = false)
            ).forEach { dao.insertCategory(it) }
        }
    }
}
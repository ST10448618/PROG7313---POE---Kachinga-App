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

    //Abstract Functions
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

        //SeedCategories Function
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

        //SeedAchievements Function
        private suspend fun seedAchievements(dao: AchievementDao) {
            listOf(
                Achievement(name = "First Saver",       description = "Added your first transaction",        icon = "🐷", xpReward = 100),
                Achievement(name = "7 Day Streak",      description = "Logged expenses for 7 days",          icon = "🔥", xpReward = 150),
                Achievement(name = "Goal Crusher",      description = "Completed first savings goal",        icon = "🎯", xpReward = 200),
                Achievement(name = "Big Spender",       description = "Saved R2000 total",                   icon = "⭐", xpReward = 300),
                Achievement(name = "Champion",          description = "30 day streak",                       icon = "🏅", xpReward = 400),
                Achievement(name = "Royalty",           description = "Reach level 10",                      icon = "👑", xpReward = 500),
                Achievement(name = "Budget Master",     description = "Stayed under budget for a month",     icon = "📊", xpReward = 250),
                Achievement(name = "Consistent Saver",  description = "Added income 5 times",                icon = "💎", xpReward = 200),
                Achievement(name = "Category Creator",  description = "Created a custom category",           icon = "🗂", xpReward = 100),
                Achievement(name = "Half Way There",    description = "Reached 50% of a savings goal",      icon = "🚀", xpReward = 150),
                Achievement(name = "Receipt Keeper",    description = "Attached a receipt to a transaction", icon = "🧾", xpReward = 100),
                Achievement(name = "High Earner",       description = "Logged income over R10,000",          icon = "💸", xpReward = 350),
                Achievement(name = "Frugal Five",       description = "Added 5 expense transactions",        icon = "✂", xpReward = 150),
                Achievement(name = "Multi Goal",        description = "Created 3 or more savings goals",     icon = "🎯", xpReward = 200),
                Achievement(name = "Profile Pro",       description = "Set your salary and savings target",  icon = "👤", xpReward = 100)
            ).forEach { dao.insertAchievement(it) }
        }
    }
}

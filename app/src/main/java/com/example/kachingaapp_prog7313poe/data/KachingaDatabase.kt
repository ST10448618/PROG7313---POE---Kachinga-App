package com.example.kachingaapp_prog7313poe.data

import android.content.Context
import androidx.room.*
import com.example.kachingaapp_prog7313poe.data.dao.*
import com.example.kachingaapp_prog7313poe.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(
    entities = [
        User::class,
        AppTransaction::class,
        Category::class,
        SavingsGoal::class,
        Achievement::class
    ],
    version = 7, // Increased after db alteration
    exportSchema = false
)
abstract class KachingaDatabase : RoomDatabase() {

    // Abstract Functions
    abstract fun userDao(): UserDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun achievementDao(): AchievementDao

    abstract fun billDao(): BillDao


    companion object {
        @Volatile
        private var INSTANCE: KachingaDatabase? = null

        fun getDatabase(context: Context): KachingaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KachingaDatabase::class.java,
                    "kachinga_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                //Runs a guaranteed safety check sequence on an independent background thread
                CoroutineScope(Dispatchers.IO).launch {
                    ensureDatabaseSeeded(instance)
                }

                instance
            }
        }

        private suspend fun ensureDatabaseSeeded(database: KachingaDatabase) {
            val categoryDao = database.categoryDao()
            val achievementDao = database.achievementDao()

            //checks if the table layout contains data elements
            if (categoryDao.getStaticCategoriesCount() == 0) {
                seedCategories(categoryDao)
            }
            if (achievementDao.getStaticAchievementsCount() == 0) {
                seedAchievements(achievementDao)
            }
        }

        private suspend fun seedCategories(dao: CategoryDao) {
            listOf(
                Category(userId = "", name = "Food",          icon = "🍔", isExpense = true),
                Category(userId = "", name = "Transport",     icon = "🚌", isExpense = true),
                Category(userId = "", name = "Medicine",      icon = "💊", isExpense = true),
                Category(userId = "", name = "Travel",        icon = "✈",  isExpense = true),
                Category(userId = "", name = "Property",      icon = "🏠", isExpense = true),
                Category(userId = "", name = "Car",           icon = "🚗", isExpense = true),
                Category(userId = "", name = "Grocery",       icon = "🛒", isExpense = true),
                Category(userId = "", name = "Rent",          icon = "🏢", isExpense = true),
                Category(userId = "", name = "Gifts",         icon = "🎁", isExpense = true),
                Category(userId = "", name = "Entertainment", icon = "🎬", isExpense = true),
                Category(userId = "", name = "Salary",        icon = "💵", isExpense = false),
                Category(userId = "", name = "Freelance",     icon = "💻", isExpense = false),
                Category(userId = "", name = "Investment",    icon = "📈", isExpense = false),
                Category(userId = "", name = "Other Income",  icon = "💰", isExpense = false)
            ).forEach { dao.insertCategory(it) }
        }

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
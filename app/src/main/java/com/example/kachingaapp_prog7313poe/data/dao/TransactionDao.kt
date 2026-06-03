package com.example.kachingaapp_prog7313poe.data.dao

import androidx.room.*
import com.example.kachingaapp_prog7313poe.data.entity.AppTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: AppTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: AppTransaction)

    @Update
    suspend fun updateTransaction(transaction: AppTransaction)
  // UserId now uses string
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY date DESC")
    fun getAllTransactions(userId: String): Flow<List<AppTransaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND isExpense = 0 ORDER BY date DESC")
    fun getAllIncome(userId: String): Flow<List<AppTransaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND isExpense = 1 ORDER BY date DESC")
    fun getAllExpenses(userId: String): Flow<List<AppTransaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND categoryId = :categoryId ORDER BY date DESC")
    fun getTransactionsByCategory(userId: String, categoryId: Int): Flow<List<AppTransaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getTransactionsByDateRange(userId: String, start: Long, end: Long): Flow<List<AppTransaction>>

    @Query("SELECT * FROM transactions WHERE userId = :userId AND isExpense = 1 AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesByDateRange(userId: String, start: Long, end: Long): Flow<List<AppTransaction>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE userId = :userId AND isExpense = 0")
    fun getTotalIncome(userId: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE userId = :userId AND isExpense = 1")
    fun getTotalExpenses(userId: String): Flow<Double>

    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId")
    suspend fun getTransactionCount(userId: String): Int
}
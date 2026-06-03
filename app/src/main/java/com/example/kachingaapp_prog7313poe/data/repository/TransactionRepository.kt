package com.example.kachingaapp_prog7313poe.data.repository

import com.example.kachingaapp_prog7313poe.data.dao.TransactionDao
import com.example.kachingaapp_prog7313poe.data.entity.AppTransaction
import kotlinx.coroutines.flow.Flow


class TransactionRepository(private val dao: TransactionDao) {

    //Transaction Functions created
    //UserId now uses string for firebase integration
    fun getAllTransactions(userId: String): Flow<List<AppTransaction>> =
        dao.getAllTransactions(userId)

    fun getTotalIncome(userId: String): Flow<Double> = dao.getTotalIncome(userId)

    fun getTotalExpenses(userId: String): Flow<Double> = dao.getTotalExpenses(userId)

    fun getByCategory(userId: String, categoryId: Int): Flow<List<AppTransaction>> =
        dao.getTransactionsByCategory(userId, categoryId)

    fun getByDateRange(userId: String, start: Long, end: Long): Flow<List<AppTransaction>> =
        dao.getTransactionsByDateRange(userId, start, end)

    fun getExpensesByDateRange(userId: String, start: Long, end: Long): Flow<List<AppTransaction>> =
        dao.getExpensesByDateRange(userId, start, end)

    //Insert Function created
    suspend fun insert(transaction: AppTransaction): Result<Unit> {
        return try {
            if (transaction.title.isBlank())
                return Result.failure(Exception("Title cannot be empty"))
            if (transaction.amount <= 0)
                return Result.failure(Exception("Amount must be greater than 0"))
            dao.insertTransaction(transaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Delete Function Created
    suspend fun delete(transaction: AppTransaction) = dao.deleteTransaction(transaction)

    //Uodate Function Created
    suspend fun update(transaction: AppTransaction) = dao.updateTransaction(transaction)
}

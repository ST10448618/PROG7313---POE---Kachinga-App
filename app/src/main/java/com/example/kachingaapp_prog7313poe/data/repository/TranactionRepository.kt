package com.example.kachingaapp_prog7313poe.data.repository

import com.example.kachingaapp_prog7313poe.data.dao.TransactionDao
import com.example.kachingaapp_prog7313poe.data.entity.AppTransaction
import kotlinx.coroutines.flow.Flow


class TransactionRepository(private val dao: TransactionDao) {

    fun getAllTransactions(userId: Int): Flow<List<AppTransaction>> =
        dao.getAllTransactions(userId)

    fun getTotalIncome(userId: Int): Flow<Double> = dao.getTotalIncome(userId)

    fun getTotalExpenses(userId: Int): Flow<Double> = dao.getTotalExpenses(userId)

    fun getByCategory(userId: Int, categoryId: Int): Flow<List<AppTransaction>> =
        dao.getTransactionsByCategory(userId, categoryId)


}

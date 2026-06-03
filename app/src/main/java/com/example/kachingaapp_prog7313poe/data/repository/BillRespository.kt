package com.example.kachingaapp_prog7313poe.data.repository

import com.example.kachingaapp_prog7313poe.data.dao.BillDao
import com.example.kachingaapp_prog7313poe.data.entity.Bill
import kotlinx.coroutines.flow.Flow
import kotlin.compareTo

class BillRepository(private val dao: BillDao) {

    fun getAllBills(userId: Int): Flow<List<Bill>> = dao.getAllBills(userId)

    suspend fun insert(bill: Bill): Result<Unit> {
        return try {
            if (bill.name.isBlank())
                return Result.failure(Exception("Bill name cannot be empty"))
            if (bill.amount <= 0)
                return Result.failure(Exception("Amount must be greater than 0"))
            if (bill.dueDay !in 1..31)
                return Result.failure(Exception("Due day must be between 1 and 31"))
            dao.insertBill(bill)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(bill: Bill) = dao.deleteBill(bill)

    suspend fun update(bill: Bill) = dao.updateBill(bill)

    suspend fun deactivate(billId: Int) = dao.deactivateBill(billId)

    suspend fun getBillsForMonth(userId: Int): List<Bill> = dao.getBillsForMonth(userId)
}

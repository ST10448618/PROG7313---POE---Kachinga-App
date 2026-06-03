package com.example.kachingaapp_prog7313poe.data.dao;

import androidx.room.*;
import com.example.kachingaapp_prog7313poe.data.entity.Bill;
import kotlinx.coroutines.flow.Flow;

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill)

    @Delete
    suspend fun deleteBill(bill: Bill)

    @Update
    suspend fun updateBill(bill: Bill)

    @Query("SELECT * FROM bills WHERE userId = :userId AND isActive = 1 ORDER BY dueDay ASC")
    fun getAllBills(userId: Int): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE userId = :userId AND isActive = 1")
    suspend fun getBillsForMonth(userId: Int): List<Bill>

    @Query("UPDATE bills SET isActive = 0 WHERE id = :billId")
    suspend fun deactivateBill(billId: Int)

    @Query("SELECT COUNT(*) FROM bills WHERE userId = :userId AND isActive = 1")
    suspend fun getBillCount(userId: Int): Int
}
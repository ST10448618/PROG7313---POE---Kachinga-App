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
}

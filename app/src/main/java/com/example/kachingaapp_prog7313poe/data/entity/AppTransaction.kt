package com.example.kachingaapp_prog7313poe.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "transactions")
data class AppTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int = 0,
    val title: String,
    val description: String = "",
    val amount: Double,
    val categoryId: Int,
    val categoryName: String,
    val categoryIcon: String,
    val isExpense: Boolean,
    val note: String = "",
    val imagePath: String = "",
    val date: Long = System.currentTimeMillis(),
    val startTime: String = "",
    val endTime: String = ""
)
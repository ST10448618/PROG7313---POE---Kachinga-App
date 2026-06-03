package com.example.kachingaapp_prog7313poe.data.entity;

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int = 0,
    val name: String,
    val amount: Double,
    val dueDay: Int, // Day of month (1-31)
    val category: String, // "Rent", "Utilities", "Subscription", etc.
    val icon: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
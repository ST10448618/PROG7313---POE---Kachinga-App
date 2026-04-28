package com.example.kachingaapp_prog7313poe.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val email: String,
    val password: String,
    val monthlyIncome: Double = 0.0,
    val currency: String = "ZAR (R)",
    val minMonthlyGoal: Double = 0.0,
    val maxMonthlyGoal: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
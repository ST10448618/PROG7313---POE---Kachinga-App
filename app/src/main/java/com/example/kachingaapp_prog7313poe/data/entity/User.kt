package com.example.kachingaapp_prog7313poe.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey //No longer auto-generated as firebase uses UUID
    val id: String,
    val fullName: String,
    val email: String,
    //Password removed as it is now stored in firebase
    val monthlyIncome: Double = 0.0,
    val currency: String = "ZAR (R)",
    val minMonthlyGoal: Double = 0.0,
    val maxMonthlyGoal: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
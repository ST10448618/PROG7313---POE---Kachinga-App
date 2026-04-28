package com.example.kachingaapp_prog7313poe.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int = 0,
    val name: String,
    val icon: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
package com.example.kachingaapp_prog7313poe.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val icon: String,
    val isEarned: Boolean = false,
    val earnedAt: Long = 0L,
    val xpReward: Int = 100
)
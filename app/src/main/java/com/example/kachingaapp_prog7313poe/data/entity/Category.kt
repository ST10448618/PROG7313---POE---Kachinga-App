package com.example.kachingaapp_prog7313poe.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int = 0,
    val name: String,
    val icon: String,
    val isExpense: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
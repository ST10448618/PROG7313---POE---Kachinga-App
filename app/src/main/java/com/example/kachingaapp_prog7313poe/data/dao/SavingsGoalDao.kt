package com.example.kachingaapp_prog7313poe.data.dao

import androidx.room.*
import com.example.kachingaapp_prog7313poe.data.entity.SavingsGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsGoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoal)

    @Delete
    suspend fun deleteGoal(goal: SavingsGoal)

    @Update
    suspend fun updateGoal(goal: SavingsGoal)
 // UserId now uses string
    @Query("SELECT * FROM savings_goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllGoals(userId: String): Flow<List<SavingsGoal>>

    @Query("UPDATE savings_goals SET savedAmount = savedAmount + :amount WHERE id = :goalId AND userId = :userId")
    suspend fun addDeposit(goalId: Int, userId: String, amount: Double)

    @Query("SELECT * FROM savings_goals WHERE id = :id AND userId = :userId")
    fun getGoalById(id: Int, userId: String): Flow<SavingsGoal?>
}
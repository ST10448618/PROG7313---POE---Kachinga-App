package com.example.kachingaapp_prog7313poe.data.repository

import com.example.kachingaapp_prog7313poe.data.dao.SavingsGoalDao
import com.example.kachingaapp_prog7313poe.data.entity.SavingsGoal
import kotlinx.coroutines.flow.Flow
import kotlin.compareTo

class SavingsRepository(private val dao: SavingsGoalDao) {

    //Functions for Savings
    fun getAll(userId: Int): Flow<List<SavingsGoal>> = dao.getAllGoals(userId)

    fun getById(id: Int, userId: Int): Flow<SavingsGoal?> = dao.getGoalById(id, userId)

    //Insert Savings Function
    suspend fun insert(goal: SavingsGoal): Result<Unit> {
        return try {
            if (goal.name.isBlank())
                return Result.failure(Exception("Goal name cannot be empty"))
            if (goal.targetAmount <= 0)
                return Result.failure(Exception("Target must be greater than 0"))
            dao.insertGoal(goal)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Deposit Savings Function
    suspend fun deposit(goalId: Int, userId: Int, amount: Double): Result<Unit> {
        return try {
            if (amount <= 0)
                return Result.failure(Exception("Deposit must be greater than 0"))
            dao.addDeposit(goalId, userId, amount)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Delete Savings Function
    suspend fun delete(goal: SavingsGoal) = dao.deleteGoal(goal)
}
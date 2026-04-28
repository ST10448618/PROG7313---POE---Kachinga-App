package com.example.kachingaapp_prog7313poe.data.repository

import com.example.kachingaapp_prog7313poe.data.dao.UserDao
import com.example.kachingaapp_prog7313poe.data.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {

    //Register Function Created
    suspend fun register(user: User): Result<Unit> {
        return try {
            val existing = dao.getUserByEmail(user.email.trim())
            if (existing != null) {
                return Result.failure(Exception("Email already registered"))
            }
            dao.insertUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Registration failed: ${e.message}"))
        }
    }

    //Login Function Created
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val trimmedEmail = email.trim()
            val trimmedPassword = password.trim()

            // Try exact match first
            var user = dao.login(trimmedEmail, trimmedPassword)

            // If null try with lowercase email
            if (user == null) {
                user = dao.login(trimmedEmail.lowercase(), trimmedPassword)
            }

            // If still null fetch by email and check password manually
            // This handles any encoding/collation differences on device
            if (user == null) {
                val foundByEmail = dao.getUserByEmailOnly(trimmedEmail)
                    ?: dao.getUserByEmailOnly(trimmedEmail.lowercase())

                if (foundByEmail != null && foundByEmail.password == trimmedPassword) {
                    user = foundByEmail
                }
            }

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Login error: ${e.message}"))
        }
    }

    fun getUserById(id: Int): Flow<User?> = dao.getUserById(id)

    suspend fun updateUser(user: User) = dao.updateUser(user)

    suspend fun getUserByEmail(email: String): User? {
        return try {
            dao.getUserByEmail(email.trim())
                ?: dao.getUserByEmail(email.trim().lowercase())
        } catch (e: Exception) {
            null
        }
    }
}
package com.example.kachingaapp_prog7313poe.data.repository

import com.example.kachingaapp_prog7313poe.data.dao.UserDao
import com.example.kachingaapp_prog7313poe.data.entity.User
import kotlinx.coroutines.flow.Flow
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class UserRepository(private val dao: UserDao) {

    //Register Function Created
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    //Firebase handles registration, then we save the metadata locally
    suspend fun register(fullName: String, email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Firebase user creation failed")

            val newUser = User(
                id = firebaseUser.uid, //Connects Room record directly to Firebase UID
                fullName = fullName,
                email = email
            )
            dao.insertUser(newUser)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Registration failed"))
        }
    }

    //Login Function Created
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User session not found")

            val localUser = dao.getUserByIdImmediate(firebaseUser.uid)
                ?: throw Exception("User profile missing from local database")

            Result.success(localUser)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Invalid email or password"))
        }
    }

    fun getUserById(id: String): Flow<User?> = dao.getUserById(id)

    suspend fun updateUser(user: User) = dao.updateUser(user)

    suspend fun getUserByEmail(email: String): User? {
        return try {
            dao.getUserByEmail(email.trim())
                ?: dao.getUserByEmail(email.trim().lowercase())
        } catch (e: Exception) {
            null
        }
    }

    fun firebaseSignOut() {
        firebaseAuth.signOut()
    }
}
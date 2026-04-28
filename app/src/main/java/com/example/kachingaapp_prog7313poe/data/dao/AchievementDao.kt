package com.example.kachingaapp_prog7313poe.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kachingaapp_prog7313poe.data.entity.Achievement
import kotlinx.coroutines.flow.Flow


@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement)

    @Update
    suspend fun updateAchievement(achievement: Achievement)

    @Query("SELECT * FROM achievements ORDER BY isEarned DESC, name ASC")
    fun getAllAchievements(): Flow<List<Achievement>>

    @Query("SELECT * FROM achievements WHERE isEarned = 1")
    fun getEarnedAchievements(): Flow<List<Achievement>>

    @Query("UPDATE achievements SET isEarned = 1, earnedAt = :timestamp WHERE name = :name")
    suspend fun awardAchievement(name: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM achievements WHERE isEarned = 1")
    fun getEarnedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getAchievementCount(): Int
}
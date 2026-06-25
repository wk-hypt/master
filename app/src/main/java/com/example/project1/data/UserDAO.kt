package com.example.project1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users ORDER BY weeklyPoints DESC")
    fun getWeeklyLeaderboardStream(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY plasticsSaved DESC")
    fun getPlasticsLeaderboardStream(): Flow<List<UserEntity>>

    @Query("UPDATE users SET weeklyPoints = 0")
    suspend fun resetWeeklyPoints()

    @Query("SELECT * FROM users WHERE studentId = :studentId")
    fun getUserStream(studentId: String): Flow<UserEntity?>
}
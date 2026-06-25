package com.example.project1.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmissionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: EcoSubmissionEntity)

    @Update
    suspend fun updateSubmission(submission: EcoSubmissionEntity)

    @Delete
    suspend fun deleteSubmission(submission: EcoSubmissionEntity)

    @Query("SELECT * FROM user_submissions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>>
}
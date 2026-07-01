package com.example.project1.data.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.project1.data.entity.EcoSubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmissionDAO {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSubmission(submission: EcoSubmissionEntity)

    @Update
    suspend fun updateSubmission(submission: EcoSubmissionEntity)

    @Delete
    suspend fun deleteSubmission(submission: EcoSubmissionEntity)

    @Query("SELECT * FROM user_submissions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>>

    @Query("SELECT * FROM user_submissions WHERE status = 'Pending' ORDER BY timestamp DESC")
    fun getAllPendingSubmissionsStream(): Flow<List<EcoSubmissionEntity>>

    @Query("UPDATE user_submissions SET status = :status WHERE id = :submissionId")
    suspend fun updateStatus(submissionId: Int, status: String)
}
package com.example.project1.data.repository

import android.util.Log
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.NewSubmission
import com.example.project1.data.model.StatusUpdate
import com.example.project1.data.model.SubmissionReviewUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface SubmissionRepository {
    fun getAllSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>>
    fun getAllPendingSubmissionsStream(): Flow<List<EcoSubmissionEntity>>
    fun getReportSubmissionsStream(): Flow<List<EcoSubmissionEntity>>
    fun getRejectedSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>>
    suspend fun insertSubmission(submission: EcoSubmissionEntity)
    suspend fun deleteSubmission(submission: EcoSubmissionEntity)
    suspend fun updateSubmission(submission: EcoSubmissionEntity)
    suspend fun updateStatus(submissionId: Int, status: String)
    suspend fun approveSubmission(submissionId: Int, adminId: String, points: Int)
    suspend fun rejectSubmission(submissionId: Int, adminId: String, feedback: String?)
    suspend fun getSubmissionById(submissionId: Int): EcoSubmissionEntity?
}

class SupabaseSubmissionRepository(private val postgrest: Postgrest) : SubmissionRepository {

    override fun getAllSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>> = pollingFlow {
        try {
            postgrest.from("user_submissions").select {
                filter { eq("user_id", userId) }
                order("timestamp", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Error fetching user submissions: ${e.message}", e)
            emptyList()
        }
    }

    override fun getAllPendingSubmissionsStream(): Flow<List<EcoSubmissionEntity>> = pollingFlow {
        try {
            postgrest.from("user_submissions").select {
                filter { eq("status", "Pending") }
                order("timestamp", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Error fetching pending submissions: ${e.message}", e)
            emptyList()
        }
    }

    override fun getReportSubmissionsStream(): Flow<List<EcoSubmissionEntity>> = pollingFlow {
        try {
            postgrest.from("user_submissions").select {
                order("timestamp", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Error fetching report submissions: ${e.message}", e)
            emptyList()
        }
    }

    override fun getRejectedSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>> = pollingFlow {
        try {
            postgrest.from("user_submissions").select {
                filter {
                    eq("user_id", userId)
                    eq("status", "Rejected")
                }
                order("timestamp", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Error fetching rejected submissions: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun insertSubmission(submission: EcoSubmissionEntity) {
        try {
            postgrest.from("user_submissions").insert(
                NewSubmission(
                    userId = submission.userId,
                    actionType = submission.actionType,
                    stallName = submission.stallName,
                    imagePath = submission.imagePath,
                    status = submission.status,
                    timestamp = submission.timestamp,
                    quantity = submission.quantity,
                    description = submission.description,
                    location = submission.location
                )
            )
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to insert submission: ${e.message}", e)
        }
    }

    override suspend fun deleteSubmission(submission: EcoSubmissionEntity) {
        try {
            postgrest.from("user_submissions").delete {
                filter { eq("id", submission.id) }
            }
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to delete submission: ${e.message}", e)
        }
    }

    override suspend fun updateSubmission(submission: EcoSubmissionEntity) {
        try {
            postgrest.from("user_submissions").update(
                NewSubmission(
                    userId = submission.userId,
                    actionType = submission.actionType,
                    stallName = submission.stallName,
                    imagePath = submission.imagePath,
                    status = submission.status,
                    timestamp = submission.timestamp,
                    quantity = submission.quantity,
                    description = submission.description,
                    location = submission.location
                )
            ) {
                filter { eq("id", submission.id) }
            }
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to update submission: ${e.message}", e)
        }
    }

    override suspend fun updateStatus(submissionId: Int, status: String) {
        try {
            postgrest.from("user_submissions").update(
                StatusUpdate(status = status)
            ) {
                filter { eq("id", submissionId) }
            }
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to update status: ${e.message}", e)
        }
    }

    override suspend fun approveSubmission(submissionId: Int, adminId: String, points: Int) {
        try {
            postgrest.from("user_submissions").update(
                SubmissionReviewUpdate(
                    status = "Approved",
                    points = points,
                    reviewedBy = adminId,
                    reviewTimestamp = System.currentTimeMillis()
                )
            ) {
                filter { eq("id", submissionId) }
            }
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to approve submission: ${e.message}", e)
        }
    }

    override suspend fun rejectSubmission(submissionId: Int, adminId: String, feedback: String?) {
        try {
            postgrest.from("user_submissions").update(
                SubmissionReviewUpdate(
                    status = "Rejected",
                    points = 0,
                    reviewedBy = adminId,
                    adminFeedback = feedback,
                    reviewTimestamp = System.currentTimeMillis()
                )
            ) {
                filter { eq("id", submissionId) }
            }
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to reject submission: ${e.message}", e)
        }
    }

    override suspend fun getSubmissionById(submissionId: Int): EcoSubmissionEntity? {
        return try {
            postgrest.from("user_submissions").select {
                filter { eq("id", submissionId) }
            }.decodeSingleOrNull()
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to get submission by ID: ${e.message}", e)
            null
        }
    }
}
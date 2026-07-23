package com.example.project1.data.repository

import com.example.project1.data.entity.EcoSubmissionEntity
import com.example.project1.data.entity.NewSubmission
import com.example.project1.data.entity.StatusUpdate
import com.example.project1.data.entity.SubmissionReviewUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface SubmissionRepository {
    fun getAllSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>>
    fun getAllPendingSubmissionsStream(): Flow<List<EcoSubmissionEntity>>
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
        postgrest.from("user_submissions").select {
            filter { eq("user_id", userId) }
            order("timestamp", Order.DESCENDING)
        }.decodeList()
    }

    override fun getAllPendingSubmissionsStream(): Flow<List<EcoSubmissionEntity>> = pollingFlow {
        postgrest.from("user_submissions").select {
            filter { eq("status", "Pending") }
            order("timestamp", Order.DESCENDING)
        }.decodeList()
    }

    override fun getRejectedSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>> = pollingFlow {
        postgrest.from("user_submissions").select {
            filter {
                eq("user_id", userId)
                eq("status", "Rejected")
            }
            order("timestamp", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun insertSubmission(submission: EcoSubmissionEntity) {
        postgrest.from("user_submissions").insert(
            NewSubmission(
                userId = submission.userId,
                actionType = submission.actionType,
                stallName = submission.stallName,
                imagePath = submission.imagePath,
                timestamp = submission.timestamp,
                quantity = submission.quantity,
                description = submission.description,
                location = submission.location
            )
        )
    }

    override suspend fun deleteSubmission(submission: EcoSubmissionEntity) {
        postgrest.from("user_submissions").delete { filter { eq("id", submission.id) } }
    }

    override suspend fun updateSubmission(submission: EcoSubmissionEntity) {
        postgrest.from("user_submissions").update(submission) {
            filter { eq("id", submission.id) }
        }
    }

    override suspend fun updateStatus(submissionId: Int, status: String) {
        postgrest.from("user_submissions").update(StatusUpdate(status = status)) {
            filter { eq("id", submissionId) }
        }
    }

    override suspend fun approveSubmission(submissionId: Int, adminId: String, points: Int) {
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
    }

    override suspend fun rejectSubmission(submissionId: Int, adminId: String, feedback: String?) {
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
    }

    override suspend fun getSubmissionById(submissionId: Int): EcoSubmissionEntity? {
        return postgrest.from("user_submissions").select {
            filter { eq("id", submissionId) }
        }.decodeSingleOrNull()
    }
}
package com.example.project1.data.repository

import android.util.Log
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.NewSubmission
import com.example.project1.data.model.StatusUpdate
import com.example.project1.data.model.SubmissionReviewUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

// interface for eco submissions crud
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
    suspend fun uploadProofImage(bytes: ByteArray): String
}

// concrete class to implement user submissions
class SupabaseSubmissionRepository(
    private val postgrest: Postgrest,
    private val storage: Storage
) : SubmissionRepository {

    // read all submissions stream for specific user (r)
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

    // read all pending submissions stream for admin review (r)
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

    // read all submissions stream for admin reports (r)
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

    // read rejected submissions stream for specific user (r)
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

    // insert a new user submission (c)
    override suspend fun insertSubmission(submission: EcoSubmissionEntity) {
        withContext(Dispatchers.IO) {
            try {
                val publicImageUrl = resolveAndUploadImage(submission.imagePath)

                postgrest.from("user_submissions").insert(
                    NewSubmission(
                        userId = submission.userId,
                        actionType = submission.actionType,
                        stallName = submission.stallName,
                        imagePath = publicImageUrl,
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
    }

    // delete a submission by ID (d)
    override suspend fun deleteSubmission(submission: EcoSubmissionEntity) {
        try {
            postgrest.from("user_submissions").delete {
                filter { eq("id", submission.id) }
            }
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to delete submission: ${e.message}", e)
        }
    }

    // update existing submission details (u)
    override suspend fun updateSubmission(submission: EcoSubmissionEntity) {
        withContext(Dispatchers.IO) {
            try {
                val publicImageUrl = resolveAndUploadImage(submission.imagePath)

                postgrest.from("user_submissions").update(
                    NewSubmission(
                        userId = submission.userId,
                        actionType = submission.actionType,
                        stallName = submission.stallName,
                        imagePath = publicImageUrl,
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
    }

    // update submission status (u)
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

    // approve submission and reward points (u)
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

    // reject submission with feedback (u)
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

    // read single submission by ID (r)
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

    // upload image file to storage bucket (supa)
    override suspend fun uploadProofImage(bytes: ByteArray): String = withContext(Dispatchers.IO) {
        val fileName = "eco-logs/${UUID.randomUUID()}.jpg"
        val bucket = storage.from("vouchers")
        bucket.upload(fileName, bytes, upsert = true)
        return@withContext bucket.publicUrl(fileName)
    }

    // resolve local image path and upload to remote storage
    private suspend fun resolveAndUploadImage(path: String): String {
        if (path.isBlank()) return path
        if (path.startsWith("http://", ignoreCase = true) || path.startsWith("https://", ignoreCase = true)) {
            return path
        }
        return try {
            val file = File(path)
            if (file.exists()) {
                uploadProofImage(file.readBytes())
            } else {
                path
            }
        } catch (e: Exception) {
            Log.e("SubmissionRepository", "Failed to upload local image path: ${e.message}", e)
            path
        }
    }
}
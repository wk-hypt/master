package com.example.project1.data.repository

import com.example.project1.data.DAO.SubmissionDAO
import com.example.project1.data.entity.EcoSubmissionEntity
import kotlinx.coroutines.flow.Flow

interface SubmissionRepository {
    fun getAllSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>>
    suspend fun insertSubmission(submission: EcoSubmissionEntity)
    suspend fun deleteSubmission(submission: EcoSubmissionEntity)
    suspend fun updateSubmission(submission: EcoSubmissionEntity)
}

class OfflineSubmissionRepository(private val submissionDAO: SubmissionDAO) : SubmissionRepository {
    override fun getAllSubmissionsStream(userId: String): Flow<List<EcoSubmissionEntity>> = submissionDAO.getAllSubmissionsStream(userId)
    override suspend fun insertSubmission(submission: EcoSubmissionEntity) = submissionDAO.insertSubmission(submission)
    override suspend fun deleteSubmission(submission: EcoSubmissionEntity) = submissionDAO.deleteSubmission(submission)
    override suspend fun updateSubmission(submission: EcoSubmissionEntity) = submissionDAO.updateSubmission(submission)
}
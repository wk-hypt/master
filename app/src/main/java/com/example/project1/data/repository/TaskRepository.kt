package com.example.project1.data.repository

import android.util.Log
import com.example.project1.data.model.NewTask
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.TaskProofUpdate
import com.example.project1.data.model.TaskReviewUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasksStream(userId: String): Flow<List<TaskEntity>>
    fun getAllPendingTasksStream(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun submitTaskProof(taskId: Int, imagePath: String)
    suspend fun approveTask(taskId: Int, adminId: String, points: Int, plasticSaved: Int)
    suspend fun rejectTask(taskId: Int, adminId: String, feedback: String?)
    suspend fun deleteTask(taskId: Int)
    suspend fun getTaskById(taskId: Int): TaskEntity?
}

class SupabaseTaskRepository(private val postgrest: Postgrest) : TaskRepository {

    override fun getAllTasksStream(userId: String): Flow<List<TaskEntity>> = pollingFlow {
        try {
            postgrest.from("user_tasks").select {
                filter { eq("user_id", userId) }
                order("timestamp", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Error fetching tasks: ${e.message}")
            emptyList()
        }
    }

    override fun getAllPendingTasksStream(): Flow<List<TaskEntity>> = pollingFlow {
        try {
            postgrest.from("user_tasks").select {
                filter { eq("status", "Pending") }
                order("timestamp", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Error fetching pending tasks: ${e.message}")
            emptyList()
        }
    }

    override suspend fun insertTask(task: TaskEntity) {
        try {
            postgrest.from("user_tasks").insert(
                NewTask(
                    userId = task.userId,
                    title = task.title,
                    description = task.description,
                    status = task.status,
                    targetQuantity = task.targetQuantity,
                    deadline = task.deadline,
                    timestamp = task.timestamp
                )
            )
            Log.d("SupabaseTaskRepository", "Task inserted successfully")
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to insert task: ${e.message}", e)
        }
    }

    override suspend fun submitTaskProof(taskId: Int, imagePath: String) {
        try {
            postgrest.from("user_tasks").update(
                TaskProofUpdate(
                    imagePath = imagePath,
                    status = "Pending"
                )
            ) {
                filter { eq("id", taskId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to submit proof: ${e.message}", e)
        }
    }

    override suspend fun deleteTask(taskId: Int) {
        try {
            postgrest.from("user_tasks").delete {
                filter { eq("id", taskId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to delete task: ${e.message}", e)
        }
    }

    override suspend fun approveTask(taskId: Int, adminId: String, points: Int, plasticSaved: Int) {
        try {
            postgrest.from("user_tasks").update(
                TaskReviewUpdate(
                    status = "Approved",
                    points = points,
                    plasticSaved = plasticSaved,
                    reviewedBy = adminId,
                    reviewTimestamp = System.currentTimeMillis()
                )
            ) {
                filter { eq("id", taskId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to approve task: ${e.message}", e)
        }
    }

    override suspend fun rejectTask(taskId: Int, adminId: String, feedback: String?) {
        try {
            postgrest.from("user_tasks").update(
                TaskReviewUpdate(
                    status = "Rejected",
                    points = 0,
                    plasticSaved = 0,
                    reviewedBy = adminId,
                    adminFeedback = feedback,
                    reviewTimestamp = System.currentTimeMillis()
                )
            ) {
                filter { eq("id", taskId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to reject task: ${e.message}", e)
        }
    }

    override suspend fun getTaskById(taskId: Int): TaskEntity? {
        return try {
            postgrest.from("user_tasks").select {
                filter { eq("id", taskId) }
            }.decodeSingleOrNull()
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to get task by ID: ${e.message}", e)
            null
        }
    }
}
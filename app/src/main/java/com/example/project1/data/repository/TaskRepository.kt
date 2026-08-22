package com.example.project1.data.repository

import android.util.Log
import com.example.project1.data.model.NewTask
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.TaskProgressUpdate
import com.example.project1.data.model.TaskReviewUpdate
import com.example.project1.data.model.TaskStatusUpdate
import com.example.project1.data.model.UserPointsUpdate
import com.example.project1.data.model.UserProfile
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface TaskRepository {
    fun getAllTasksStream(userId: String): Flow<List<TaskEntity>>
    fun getAllPendingTasksStream(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun updateTaskProgress(taskId: Int, imagePath: String)
    suspend fun submitTaskToAdmin(taskId: Int)
    suspend fun approveTask(taskId: Int, adminId: String, points: Int, plasticSaved: Int)
    suspend fun rejectTask(taskId: Int, adminId: String, feedback: String?)
    suspend fun deleteTask(taskId: Int)
    suspend fun getTaskById(taskId: Int): TaskEntity?
    suspend fun uploadProofImage(bytes: ByteArray): String
}

class SupabaseTaskRepository(
    private val postgrest: Postgrest,
    private val storage: Storage
) : TaskRepository {

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
                    currentQuantity = task.currentQuantity,
                    taskQuantity = task.taskQuantity,
                    deadline = task.deadline,
                    timestamp = task.timestamp
                )
            )
            Log.d("SupabaseTaskRepository", "Task inserted successfully")
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to insert task: ${e.message}", e)
        }
    }

    override suspend fun updateTask(task: TaskEntity) {
        try {
            postgrest.from("user_tasks").update(
                NewTask(
                    userId = task.userId,
                    title = task.title,
                    description = task.description,
                    status = task.status,
                    currentQuantity = task.currentQuantity,
                    taskQuantity = task.taskQuantity,
                    deadline = task.deadline,
                    timestamp = task.timestamp
                )
            ) {
                filter { eq("id", task.id) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to update task: ${e.message}", e)
        }
    }

    override suspend fun updateTaskProgress(taskId: Int, imagePath: String) {
        try {
            val currentTask = getTaskById(taskId) ?: return
            val newQuantity = currentTask.currentQuantity + 1

            postgrest.from("user_tasks").update(
                TaskProgressUpdate(
                    currentQuantity = newQuantity,
                    imagePath = imagePath,
                    status = "InProgress"
                )
            ) {
                filter { eq("id", taskId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to update task progress: ${e.message}", e)
        }
    }

    override suspend fun submitTaskToAdmin(taskId: Int) {
        try {
            postgrest.from("user_tasks").update(
                mapOf("status" to "Pending")
            ) {
                filter { eq("id", taskId) }
            }
            Log.d("SupabaseTaskRepository", "Successfully updated task #$taskId status to Pending")
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to submit task to admin: ${e.message}", e)
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
            val task = getTaskById(taskId)
            if (task == null) {
                Log.e("SupabaseTaskRepository", "Task not found with ID: $taskId")
                return
            }

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

            val user = postgrest.from("users")
                .select(Columns.list("user_id", "total_points", "total_plastic_saved")) {
                    filter { eq("user_id", task.userId) }
                }.decodeSingleOrNull<UserProfile>()

            if (user != null) {
                val newTotalPoints = user.totalPoints + points
                val newTotalPlastic = user.totalPlasticSaved + plasticSaved

                postgrest.from("users").update(
                    UserPointsUpdate(
                        totalPoints = newTotalPoints,
                        totalPlasticSaved = newTotalPlastic
                    )
                ) {
                    filter { eq("user_id", task.userId) }
                }
                Log.d("SupabaseTaskRepository", "Successfully updated points for user ${task.userId}")
            } else {
                Log.e("SupabaseTaskRepository", "User record not found in users table for ID: ${task.userId}")
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

    override suspend fun uploadProofImage(bytes: ByteArray): String {
        val fileName = "task-proofs/${UUID.randomUUID()}.jpg"
        val bucket = storage.from("vouchers")
        bucket.upload(fileName, bytes, upsert = true)
        return bucket.publicUrl(fileName)
    }
}
package com.example.project1.data.repository

import android.util.Log
import com.example.project1.data.model.NewTask
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.TaskProgressUpdate
import com.example.project1.data.model.TaskReviewUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.Flow
import com.example.project1.data.pollingFlow
import java.util.UUID

// interface for task crud
interface TaskRepository {
    fun getAllTasksStream(userId: String): Flow<List<TaskEntity>>
    fun getAllPendingTasksStream(): Flow<List<TaskEntity>>
    fun getReportTasksStream(): Flow<List<TaskEntity>>
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

// concrete class to implement user tasks
class SupabaseTaskRepository(
    private val postgrest: Postgrest,
    private val storage: Storage
) : TaskRepository {

    // read all tasks stream for specific user (r)
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

    // read all pending tasks stream for admin review (r)
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

    // read all tasks stream for admin reports (r)
    override fun getReportTasksStream(): Flow<List<TaskEntity>> = pollingFlow {
        try {
            postgrest.from("user_tasks").select {
                order("timestamp", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Error fetching report tasks: ${e.message}")
            emptyList()
        }
    }

    // insert a new task (c)
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

    // update existing task details (u)
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

    // update task progress quantity and proof image (u)
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

    // submit completed task to admin for verification (u)
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

    // delete a task by ID (d)
    override suspend fun deleteTask(taskId: Int) {
        try {
            postgrest.from("user_tasks").delete {
                filter { eq("id", taskId) }
            }
        } catch (e: Exception) {
            Log.e("SupabaseTaskRepository", "Failed to delete task: ${e.message}", e)
        }
    }

    // approve task and reward points and plastic savings (u)
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

    // reject task with feedback (u)
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

    // read single task by ID (r)
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

    // upload task proof image to storage bucket
    override suspend fun uploadProofImage(bytes: ByteArray): String {
        val fileName = "task-proofs/${UUID.randomUUID()}.jpg"
        val bucket = storage.from("vouchers")
        bucket.upload(fileName, bytes, upsert = true)
        return bucket.publicUrl(fileName)
    }
}
package com.example.project1.data.repository

import com.example.project1.data.entity.NewTask
import com.example.project1.data.entity.TaskEntity
import com.example.project1.data.entity.TaskReviewUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasksStream(userId: String): Flow<List<TaskEntity>>
    fun getAllPendingTasksStream(): Flow<List<TaskEntity>>
    suspend fun insertTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun approveTask(taskId: Int, adminId: String, points: Int, plasticSaved: Int)
    suspend fun rejectTask(taskId: Int, adminId: String, feedback: String?)
    suspend fun getTaskById(taskId: Int): TaskEntity?
}

class SupabaseTaskRepository(private val postgrest: Postgrest) : TaskRepository {

    override fun getAllTasksStream(userId: String): Flow<List<TaskEntity>> = pollingFlow {
        postgrest.from("user_tasks").select {
            filter { eq("user_id", userId) }
            order("timestamp", Order.DESCENDING)
        }.decodeList()
    }

    override fun getAllPendingTasksStream(): Flow<List<TaskEntity>> = pollingFlow {
        postgrest.from("user_tasks").select {
            filter { eq("status", "Pending") }
            order("timestamp", Order.DESCENDING)
        }.decodeList()
    }

    override suspend fun insertTask(task: TaskEntity) {
        postgrest.from("user_tasks").insert(
            NewTask(
                userId = task.userId,
                title = task.title,
                description = task.description,
                imagePath = task.imagePath,
                targetQuantity = task.targetQuantity,
                timestamp = task.timestamp
            )
        )
    }

    override suspend fun deleteTask(task: TaskEntity) {
        postgrest.from("user_tasks").delete { filter { eq("id", task.id) } }
    }

    override suspend fun approveTask(taskId: Int, adminId: String, points: Int, plasticSaved: Int) {
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
    }

    override suspend fun rejectTask(taskId: Int, adminId: String, feedback: String?) {
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
    }

    override suspend fun getTaskById(taskId: Int): TaskEntity? {
        return postgrest.from("user_tasks").select {
            filter { eq("id", taskId) }
        }.decodeSingleOrNull()
    }
}
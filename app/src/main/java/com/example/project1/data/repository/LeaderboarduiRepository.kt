package com.example.project1.data.repository

import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import java.util.Calendar

interface LeaderBoarduiRepository {
    suspend fun getCurrentUserId(): String
    suspend fun getMonthlyRankings(): List<Pair<UserEntity, Int>>
    suspend fun getDailyRankings(): List<Pair<UserEntity, Int>>
}

class SupabaseLeaderboardRepository(
    private val postgrest: Postgrest,
    private val currentStudentId: () -> String
) : LeaderBoarduiRepository {

    override suspend fun getCurrentUserId(): String = currentStudentId()

    override suspend fun getMonthlyRankings(): List<Pair<UserEntity, Int>> {
        val users = postgrest.from("users").select {
            order("total_points", Order.DESCENDING)
        }.decodeList<UserEntity>()

        return users.map { it to it.totalPoints }
    }

    override suspend fun getDailyRankings(): List<Pair<UserEntity, Int>> {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val users = postgrest.from("users").select().decodeList<UserEntity>()

        val approvedSubmissionsToday = postgrest.from("user_submissions").select {
            filter {
                eq("status", "Approved")
                gte("review_timestamp", startOfDay)
            }
        }.decodeList<EcoSubmissionEntity>()

        val approvedTasksToday = postgrest.from("user_tasks").select {
            filter {
                eq("status", "Approved")
                gte("review_timestamp", startOfDay)
            }
        }.decodeList<TaskEntity>()

        val pointsByUser = mutableMapOf<String, Int>()
        approvedSubmissionsToday.forEach { pointsByUser[it.userId] = (pointsByUser[it.userId] ?: 0) + it.points }
        approvedTasksToday.forEach { pointsByUser[it.userId] = (pointsByUser[it.userId] ?: 0) + it.points }

        return users
            .mapNotNull { user ->
                val todayPoints = pointsByUser[user.studentId] ?: 0
                if (todayPoints > 0) user to todayPoints else null
            }
            .sortedByDescending { it.second }
    }
}
package com.example.project1.data.repository

import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.data.model.UserEntity
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import java.util.Calendar

// interface for leaderboard
interface LeaderBoarduiRepository {
    suspend fun getCurrentUserId(): String
    suspend fun getMonthlyRankings(): List<Pair<UserEntity, Int>>
    suspend fun getDailyRankings(): List<Pair<UserEntity, Int>>
}

// concrete class to implement leaderboard rankings
class SupabaseLeaderboardRepository(
    private val postgrest: Postgrest,
    private val currentStudentId: () -> String
) : LeaderBoarduiRepository {

    // get current logged-in user ID
    override suspend fun getCurrentUserId(): String = currentStudentId()

    // get users ranked by total points
    override suspend fun getMonthlyRankings(): List<Pair<UserEntity, Int>> {
        val users = postgrest.from("users").select {
            order("total_points", Order.DESCENDING)
        }.decodeList<UserEntity>()

        return users.map { it to it.totalPoints }
    }

    // get users ranked by points earned today
    override suspend fun getDailyRankings(): List<Pair<UserEntity, Int>> {
        // Get timestamp for start of today (00:00)
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // read all users
        val users = postgrest.from("users").select().decodeList<UserEntity>()

        // read submissions approved today
        val approvedSubmissionsToday = postgrest.from("user_submissions").select {
            filter {
                eq("status", "Approved")
                gte("review_timestamp", startOfDay)
            }
        }.decodeList<EcoSubmissionEntity>()

        // read tasks approved today
        val approvedTasksToday = postgrest.from("user_tasks").select {
            filter {
                eq("status", "Approved")
                gte("review_timestamp", startOfDay)
            }
        }.decodeList<TaskEntity>()

        // calculate total points earned today per user
        val pointsByUser = mutableMapOf<String, Int>()
        approvedSubmissionsToday.forEach { pointsByUser[it.userId] = (pointsByUser[it.userId] ?: 0) + it.points }
        approvedTasksToday.forEach { pointsByUser[it.userId] = (pointsByUser[it.userId] ?: 0) + it.points }

        // sort users by today's points ( high -> low)
        return users
            .map { user -> user to (pointsByUser[user.studentId] ?: 0) }
            .sortedByDescending { it.second }
    }
}
package com.example.project1.data.repository

import com.example.project1.data.entity.NewUser
import com.example.project1.data.entity.UserEntity
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getWeeklyLeaderboardStream(): Flow<List<UserEntity>>
    fun getPlasticsLeaderboardStream(): Flow<List<UserEntity>>
    fun getUserStream(studentId: String): Flow<UserEntity?>
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun getUserById(studentId: String): UserEntity?
}

class SupabaseUserRepository(private val postgrest: Postgrest) : UserRepository {

    override fun getWeeklyLeaderboardStream(): Flow<List<UserEntity>> = pollingFlow {
        postgrest.from("users").select {
            order("total_points", Order.DESCENDING)
        }.decodeList()
    }

    override fun getPlasticsLeaderboardStream(): Flow<List<UserEntity>> = pollingFlow {
        postgrest.from("users").select {
            order("plastics_saved", Order.DESCENDING)
        }.decodeList()
    }

    override fun getUserStream(studentId: String): Flow<UserEntity?> = pollingFlow {
        postgrest.from("users").select {
            filter { eq("student_id", studentId) }
        }.decodeSingleOrNull()
    }

    override suspend fun insertUser(user: UserEntity) {
        postgrest.from("users").insert(
            NewUser(
                studentId = user.studentId,
                name = user.name,
                password = user.password,
                faculty = user.faculty
            )
        )
    }

    override suspend fun updateUser(user: UserEntity) {
        postgrest.from("users").update(user) {
            filter { eq("student_id", user.studentId) }
        }
    }

    override suspend fun getUserById(studentId: String): UserEntity? {
        return postgrest.from("users").select {
            filter { eq("student_id", studentId) }
        }.decodeSingleOrNull()
    }
}
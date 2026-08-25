package com.example.project1.data.repository

import com.example.project1.data.model.NewUser
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.UserPasswordUpdate
import com.example.project1.data.model.UserPointsUpdate
import com.example.project1.data.model.UserProfileInfoUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getWeeklyLeaderboardStream(): Flow<List<UserEntity>>
    fun getPlasticsLeaderboardStream(): Flow<List<UserEntity>>
    fun getAllUsersStream(): Flow<List<UserEntity>>
    fun getUserStream(studentId: String): Flow<UserEntity?>
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun updateProfileInfo(
        studentId: String,
        name: String,
        faculty: String,
        phone: String,
        email: String,
        birthday: String
    )
    suspend fun updatePassword(studentId: String, newPassword: String)
    suspend fun deleteUser(studentId: String)
    suspend fun getUserById(studentId: String): UserEntity?

    /** Adds bonus points on top of a student's current total (e.g. milestone rewards). */
    suspend fun addBonusPoints(studentId: String, bonusPoints: Int)
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

    override fun getAllUsersStream(): Flow<List<UserEntity>> = pollingFlow {
        postgrest.from("users").select {
            order("total_points", Order.DESCENDING)
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

    override suspend fun updateProfileInfo(
        studentId: String,
        name: String,
        faculty: String,
        phone: String,
        email: String,
        birthday: String
    ) {
        postgrest.from("users").update(
            UserProfileInfoUpdate(
                name = name,
                faculty = faculty,
                phone = phone,
                email = email,
                birthday = birthday
            )
        ) {
            filter { eq("student_id", studentId) }
        }
    }

    override suspend fun updatePassword(studentId: String, newPassword: String) {
        postgrest.from("users").update(UserPasswordUpdate(newPassword)) {
            filter { eq("student_id", studentId) }
        }
    }

    override suspend fun deleteUser(studentId: String) {
        postgrest.from("users").delete {
            filter { eq("student_id", studentId) }
        }
    }

    override suspend fun getUserById(studentId: String): UserEntity? {
        return postgrest.from("users").select {
            filter { eq("student_id", studentId) }
        }.decodeSingleOrNull()
    }

    override suspend fun addBonusPoints(studentId: String, bonusPoints: Int) {
        if (bonusPoints <= 0) return
        val current = getUserById(studentId) ?: return
        postgrest.from("users").update(
            UserPointsUpdate(totalPoints = current.totalPoints + bonusPoints)
        ) {
            filter { eq("student_id", studentId) }
        }
    }
}
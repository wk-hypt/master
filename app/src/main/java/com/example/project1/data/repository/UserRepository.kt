package com.example.project1.data.repository

import com.example.project1.data.model.NewUser
import com.example.project1.data.model.UserEntity
import com.example.project1.data.model.UserPasswordUpdate
import com.example.project1.data.model.UserPointsUpdate
import com.example.project1.data.model.UserProfileInfoUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

// interface for user crud
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
    suspend fun addBonusPoints(studentId: String, bonusPoints: Int)
}

// concrete class to implement user repository
class SupabaseUserRepository(private val postgrest: Postgrest) : UserRepository {

    // read weekly leaderboard stream ordered by total points (r)
    override fun getWeeklyLeaderboardStream(): Flow<List<UserEntity>> = pollingFlow {
        postgrest.from("users").select {
            order("total_points", Order.DESCENDING)
        }.decodeList()
    }

    // read plastics leaderboard stream ordered by plastics saved (r)
    override fun getPlasticsLeaderboardStream(): Flow<List<UserEntity>> = pollingFlow {
        postgrest.from("users").select {
            order("plastics_saved", Order.DESCENDING)
        }.decodeList()
    }

    // read all users stream for admin view (r)
    override fun getAllUsersStream(): Flow<List<UserEntity>> = pollingFlow {
        postgrest.from("users").select {
            order("total_points", Order.DESCENDING)
        }.decodeList()
    }

    // read single user stream by student ID (r)
    override fun getUserStream(studentId: String): Flow<UserEntity?> = pollingFlow {
        postgrest.from("users").select {
            filter { eq("student_id", studentId) }
        }.decodeSingleOrNull()
    }

    // insert a new user (c)
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

    // update existing user entity details (u)
    override suspend fun updateUser(user: UserEntity) {
        postgrest.from("users").update(user) {
            filter { eq("student_id", user.studentId) }
        }
    }

    // update user profile detailed information (u)
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

    // update user account password (u)
    override suspend fun updatePassword(studentId: String, newPassword: String) {
        postgrest.from("users").update(UserPasswordUpdate(newPassword)) {
            filter { eq("student_id", studentId) }
        }
    }

    // delete a user and related user data by student ID (d)
    override suspend fun deleteUser(studentId: String) {
        postgrest.from("user_submissions").delete {
            filter { eq("user_id", studentId) }
        }
        postgrest.from("user_tasks").delete {
            filter { eq("user_id", studentId) }
        }
        try {
            postgrest.from("campus_vouchers").delete {
                filter { eq("redeemed_by", studentId) }
            }
        } catch (_: Exception) {
        }
        postgrest.from("users").delete {
            filter { eq("student_id", studentId) }
        }
    }

    // read single user entity by student ID (r)
    override suspend fun getUserById(studentId: String): UserEntity? {
        return postgrest.from("users").select {
            filter { eq("student_id", studentId) }
        }.decodeSingleOrNull()
    }

    // update user bonus total points (u)
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
package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    @SerialName("student_id") val studentId: String = "",
    val name: String = "",
    val password: String = "",
    val faculty: String = "",
    @SerialName("total_points") val totalPoints: Int = 0,
    @SerialName("plastics_saved") val plasticsSaved: Int = 0,
    val phone: String? = null,
    val email: String? = null,
    val birthday: String? = null
)

// add new user (through register)
@Serializable
data class NewUser(
    @SerialName("student_id") val studentId: String,
    val name: String,
    val password: String,
    val faculty: String
)

// user profile page (update info)
@Serializable
data class UserProfileInfoUpdate(
    val name: String,
    val faculty: String,
    val phone: String,
    val email: String,
    val birthday: String
)

// modify password
@Serializable
data class UserPasswordUpdate(
    val password: String
)

@Serializable
data class UserPointsUpdate(
    @SerialName("total_points") val totalPoints: Int
)

// calculate total points earned from submissions and tasks
fun pointsAwardedByUser(
    submissions: List<EcoSubmissionEntity>,
    tasks: List<TaskEntity>
): Map<String, Int> {
    val awarded = mutableMapOf<String, Int>()
    submissions.filter { it.status.equals("Approved", ignoreCase = true) }.forEach { submission ->
        awarded[submission.userId] = (awarded[submission.userId] ?: 0) + submission.points
    }
    tasks.filter { it.status.equals("Approved", ignoreCase = true) }.forEach { task ->
        awarded[task.userId] = (awarded[task.userId] ?: 0) + task.points
    }
    return awarded
}

// calculate total points spent to redeemed vouchers
fun pointsSpentByUser(vouchers: List<VoucherEntity>): Map<String, Int> {
    val spent = mutableMapOf<String, Int>()
    vouchers.forEach { voucher ->
        val studentId = voucher.redeemedBy?.takeIf { it.isNotBlank() } ?: return@forEach
        spent[studentId] = (spent[studentId] ?: 0) + voucher.pointsCost
    }
    return spent
}

// update user object with new total points
fun UserEntity.withAwardedPoints(
    awarded: Map<String, Int>,
    spent: Map<String, Int> = emptyMap()
): UserEntity {
    val fromAwards = awarded[studentId] ?: 0
    val remainingPlusSpent = totalPoints + (spent[studentId] ?: 0)
    return copy(totalPoints = maxOf(fromAwards, remainingPlusSpent))
}
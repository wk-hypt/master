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

@Serializable
data class NewUser(
    @SerialName("student_id") val studentId: String,
    val name: String,
    val password: String,
    val faculty: String
)

@Serializable
data class UserProfileInfoUpdate(
    val name: String,
    val faculty: String,
    val phone: String,
    val email: String,
    val birthday: String
)

@Serializable
data class UserPasswordUpdate(
    val password: String
)

/** Lifetime points awarded to each student. Spendable `totalPoints` can drop after a redeem. */
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

fun pointsSpentByUser(vouchers: List<VoucherEntity>): Map<String, Int> {
    val spent = mutableMapOf<String, Int>()
    vouchers.forEach { voucher ->
        val studentId = voucher.redeemedBy?.takeIf { it.isNotBlank() } ?: return@forEach
        spent[studentId] = (spent[studentId] ?: 0) + voucher.pointsCost
    }
    return spent
}

fun UserEntity.withAwardedPoints(
    awarded: Map<String, Int>,
    spent: Map<String, Int> = emptyMap()
): UserEntity {
    val fromAwards = awarded[studentId] ?: 0
    val remainingPlusSpent = totalPoints + (spent[studentId] ?: 0)
    return copy(totalPoints = maxOf(fromAwards, remainingPlusSpent))
}
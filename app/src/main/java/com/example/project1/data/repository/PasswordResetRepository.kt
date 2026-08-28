package com.example.project1.data.repository

import com.example.project1.data.model.NewPasswordResetRequest
import com.example.project1.data.model.PasswordResetRequestEntity
import com.example.project1.data.model.PasswordResetReviewUpdate
import com.example.project1.data.model.RESET_STATUS_APPROVED
import com.example.project1.data.model.RESET_STATUS_PENDING
import com.example.project1.data.pollingFlow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.OTP
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

// interface for password reset requests and authentication otp
interface PasswordResetRepository {
    fun getOpenRequestsStream(): Flow<List<PasswordResetRequestEntity>>
    suspend fun getOpenRequest(accountId: String): PasswordResetRequestEntity?
    suspend fun createPendingRequest(accountId: String, accountName: String, isAdmin: Boolean): PasswordResetRequestEntity?
    suspend fun updateStatus(requestId: Long, status: String, reviewedBy: String?)
    suspend fun sendEmailOtp(email: String)
    suspend fun verifyEmailOtp(email: String, code: String)
}

// concrete class to implement password reset
class SupabasePasswordResetRepository(
    private val postgrest: Postgrest,
    private val supabaseClient: SupabaseClient
) : PasswordResetRepository {

    // get stream of open or approved reset requests
    override fun getOpenRequestsStream(): Flow<List<PasswordResetRequestEntity>> = pollingFlow {
        postgrest.from("password_reset_requests").select {
            order("created_at_millis", Order.DESCENDING)
        }.decodeList<PasswordResetRequestEntity>().filter {
            it.status.equals(RESET_STATUS_PENDING, ignoreCase = true) ||
                    it.status.equals(RESET_STATUS_APPROVED, ignoreCase = true)
        }
    }

    // get open reset request for a specific account ID
    override suspend fun getOpenRequest(accountId: String): PasswordResetRequestEntity? {
        return postgrest.from("password_reset_requests").select {
            filter { eq("account_id", accountId) }
            order("created_at_millis", Order.DESCENDING)
        }.decodeList<PasswordResetRequestEntity>().firstOrNull {
            it.status.equals(RESET_STATUS_PENDING, ignoreCase = true) ||
                    it.status.equals(RESET_STATUS_APPROVED, ignoreCase = true)
        }
    }

    // add a new password reset request into supa (c)
    override suspend fun createPendingRequest(
        accountId: String,
        accountName: String,
        isAdmin: Boolean
    ): PasswordResetRequestEntity? {
        val existing = getOpenRequest(accountId)
        if (existing != null) return existing
        postgrest.from("password_reset_requests").insert(
            NewPasswordResetRequest(
                accountId = accountId,
                accountName = accountName,
                isAdmin = isAdmin,
                createdAtMillis = System.currentTimeMillis()
            )
        )
        return getOpenRequest(accountId)
    }

    // update password reset request status and reviewer details
    override suspend fun updateStatus(
        requestId: Long,
        status: String,
        reviewedBy: String?
    ) {
        postgrest.from("password_reset_requests").update(
            PasswordResetReviewUpdate(
                status = status,
                reviewedBy = reviewedBy,
                reviewedAtMillis = System.currentTimeMillis()
            )
        ) {
            filter { eq("id", requestId) }
        }
    }

    // send email otp code for authentication
    override suspend fun sendEmailOtp(email: String) {
        supabaseClient.auth.signInWith(OTP) {
            this.email = email
        }
    }

    // verify email otp code and sign out session
    override suspend fun verifyEmailOtp(email: String, code: String) {
        try {
            supabaseClient.auth.verifyEmailOtp(
                type = OtpType.Email.EMAIL,
                email = email,
                token = code
            )
        } catch (_: Exception) {
            supabaseClient.auth.verifyEmailOtp(
                type = OtpType.Email.MAGIC_LINK,
                email = email,
                token = code
            )
        }
        runCatching { supabaseClient.auth.signOut() }
    }
}
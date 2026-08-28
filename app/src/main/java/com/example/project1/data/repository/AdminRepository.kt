package com.example.project1.data.repository

import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.AdminPasswordUpdate
import com.example.project1.data.model.AdminProfileInfoUpdate
import com.example.project1.data.pollingFlow
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.Flow

// interface for admin
interface AdminRepository {
    suspend fun getAdmins(): List<AdminEntity>
    fun getAdminStream(adminId: String): Flow<AdminEntity?>
    suspend fun getAdminById(adminId: String): AdminEntity?
    suspend fun updateProfileInfo(adminId: String, name: String, faculty: String)
    suspend fun updatePassword(adminId: String, newPassword: String)
}

// concrete class of implementing "AdminRepository"
class SupabaseAdminRepository(private val postgrest: Postgrest) : AdminRepository {
    // coroutine to get admin from supa
    override suspend fun getAdmins(): List<AdminEntity> {
        return postgrest.from("admins").select().decodeList()
    }

    // stream live updates for a specific admin
    override fun getAdminStream(adminId: String): Flow<AdminEntity?> = pollingFlow {
        postgrest.from("admins").select {
            filter { eq("admin_id", adminId) }
        }.decodeSingleOrNull()
    }

    // coroutine to get admin id from supa
    override suspend fun getAdminById(adminId: String): AdminEntity? {
        return postgrest.from("admins").select {
            filter { eq("admin_id", adminId) }
        }.decodeSingleOrNull()
    }

    // coroutine to update admin info into supa
    override suspend fun updateProfileInfo(adminId: String, name: String, faculty: String) {
        postgrest.from("admins").update(
            AdminProfileInfoUpdate(name = name, faculty = faculty)
        ) {
            filter { eq("admin_id", adminId) }
        }
    }

    // coroutine to update admin password into supa
    override suspend fun updatePassword(adminId: String, newPassword: String) {
        postgrest.from("admins").update(AdminPasswordUpdate(newPassword)) {
            filter { eq("admin_id", adminId) }
        }
    }
}
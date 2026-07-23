package com.example.project1.data.repository

import com.example.project1.data.entity.AdminEntity
import io.github.jan.supabase.postgrest.Postgrest

interface AdminRepository {
    suspend fun getAdmins(): List<AdminEntity>
}

class SupabaseAdminRepository(private val postgrest: Postgrest) : AdminRepository {
    override suspend fun getAdmins(): List<AdminEntity> {
        return postgrest.from("admins").select().decodeList()
    }
}
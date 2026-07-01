package com.example.project1.data.repository

import com.example.project1.data.datasource.AssetAdminDataSource
import com.example.project1.data.entity.AdminEntity

interface AdminRepository {
    fun getAdmins(): List<AdminEntity>
}

class OfflineAdminRepository(
    private val adminDataSource: AssetAdminDataSource
) : AdminRepository {
    override fun getAdmins(): List<AdminEntity> = adminDataSource.getAdminsFromAssets()
}
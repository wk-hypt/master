package com.example.project1.data.datasource

import android.content.Context
import com.example.project1.data.entity.AdminEntity
import kotlinx.serialization.json.Json

class AssetAdminDataSource(private val context: Context) {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun getAdminsFromAssets(): List<AdminEntity> {
        return try {
            val jsonString = context.assets.open("admin.json").bufferedReader().use { it.readText() }
            jsonConfig.decodeFromString<List<AdminEntity>>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
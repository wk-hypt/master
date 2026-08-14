package com.example.project1.data.repository

import android.util.Log
import com.example.project1.data.model.NewReport
import com.example.project1.data.model.ReportEntity
import com.example.project1.data.model.ReportNotesUpdate
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getAllReportsStream(): Flow<List<ReportEntity>>
    suspend fun insertReport(report: NewReport)
    suspend fun updateReport(reportId: Int, title: String, notes: String?)
    suspend fun deleteReport(report: ReportEntity)
    suspend fun getReportById(reportId: Int): ReportEntity?
}

class SupabaseReportRepository(private val postgrest: Postgrest) : ReportRepository {

    override fun getAllReportsStream(): Flow<List<ReportEntity>> = pollingFlow {
        try {
            postgrest.from("admin_reports").select {
                order("created_at", Order.DESCENDING)
            }.decodeList()
        } catch (e: Exception) {
            Log.e("ReportRepository", "Error fetching reports: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun insertReport(report: NewReport) {
        try {
            postgrest.from("admin_reports").insert(report)
        } catch (e: Exception) {
            Log.e("ReportRepository", "Failed to insert report: ${e.message}", e)
        }
    }

    override suspend fun updateReport(reportId: Int, title: String, notes: String?) {
        try {
            postgrest.from("admin_reports").update(
                ReportNotesUpdate(title = title, notes = notes)
            ) {
                filter { eq("id", reportId) }
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "Failed to update report: ${e.message}", e)
        }
    }

    override suspend fun deleteReport(report: ReportEntity) {
        try {
            postgrest.from("admin_reports").delete {
                filter { eq("id", report.id) }
            }
        } catch (e: Exception) {
            Log.e("ReportRepository", "Failed to delete report: ${e.message}", e)
        }
    }

    override suspend fun getReportById(reportId: Int): ReportEntity? {
        return try {
            postgrest.from("admin_reports").select {
                filter { eq("id", reportId) }
            }.decodeSingleOrNull()
        } catch (e: Exception) {
            Log.e("ReportRepository", "Failed to get report: ${e.message}", e)
            null
        }
    }
}

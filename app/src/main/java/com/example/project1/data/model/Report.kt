package com.example.project1.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val reportNarrativeJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

/** Extra admin-authored fields packed into `notes` so no new database columns are required. */
@Serializable
data class ReportNarrative(
    val kind: String = NARRATIVE_KIND,
    val reference: String? = null,
    val preparedBy: String? = null,
    val department: String? = null,
    val purpose: String? = null,
    val audience: String? = null,
    val summary: String? = null,
    val findings: String? = null,
    val recommendations: String? = null,
    val notes: String? = null
) {
    companion object {
        const val NARRATIVE_KIND = "eco_report_v1"
    }
}

data class ReportFormInput(
    val title: String,
    val studentId: String? = null,
    val studentName: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val narrative: ReportNarrative = ReportNarrative()
)

fun encodeReportNarrative(narrative: ReportNarrative): String? {
    val cleaned = narrative.copy(
        preparedBy = narrative.preparedBy?.trim()?.ifBlank { null },
        department = narrative.department?.trim()?.ifBlank { null },
        purpose = narrative.purpose?.trim()?.ifBlank { null },
        audience = narrative.audience?.trim()?.ifBlank { null },
        summary = narrative.summary?.trim()?.ifBlank { null },
        findings = narrative.findings?.trim()?.ifBlank { null },
        recommendations = narrative.recommendations?.trim()?.ifBlank { null },
        notes = narrative.notes?.trim()?.ifBlank { null },
        reference = narrative.reference?.trim()?.ifBlank { null }
    )
    val hasExtras = listOf(
        cleaned.preparedBy, cleaned.department, cleaned.purpose, cleaned.audience,
        cleaned.summary, cleaned.findings, cleaned.recommendations, cleaned.reference
    ).any { !it.isNullOrBlank() }
    if (!hasExtras && cleaned.notes.isNullOrBlank()) return null
    return reportNarrativeJson.encodeToString(ReportNarrative.serializer(), cleaned)
}

fun decodeReportNarrative(raw: String?): ReportNarrative {
    if (raw.isNullOrBlank()) return ReportNarrative()
    val trimmed = raw.trim()
    if (!trimmed.startsWith("{")) return ReportNarrative(notes = raw)
    return try {
        val parsed = reportNarrativeJson.decodeFromString(ReportNarrative.serializer(), trimmed)
        if (parsed.kind == ReportNarrative.NARRATIVE_KIND) parsed else ReportNarrative(notes = raw)
    } catch (_: Exception) {
        ReportNarrative(notes = raw)
    }
}

fun ReportEntity.narrative(): ReportNarrative = decodeReportNarrative(notes)

fun ReportEntity.displayReference(): String {
    val stored = narrative().reference?.trim().orEmpty()
    if (stored.isNotBlank()) return stored
    return "RPT-" + id.toString().padStart(4, '0')
}

fun ReportEntity.periodLabel(): String {
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return when {
        periodStart != null && periodEnd != null ->
            "${format.format(Date(periodStart))} \u2013 ${format.format(Date(periodEnd))}"
        periodStart != null -> "From ${format.format(Date(periodStart))}"
        periodEnd != null -> "Until ${format.format(Date(periodEnd))}"
        else -> "All time"
    }
}

fun newReportReference(nowMillis: Long = System.currentTimeMillis()): String {
    val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date(nowMillis))
    return "RPT-$stamp"
}

@Serializable
data class ReportEntity(
    val id: Int = 0,
    val title: String,
    val notes: String? = null,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_at") val createdAt: Long = System.currentTimeMillis(),
    @SerialName("total_submissions") val totalSubmissions: Int = 0,
    @SerialName("approved_count") val approvedCount: Int = 0,
    @SerialName("pending_count") val pendingCount: Int = 0,
    @SerialName("rejected_count") val rejectedCount: Int = 0,
    @SerialName("total_points_awarded") val totalPointsAwarded: Int = 0,
    @SerialName("total_plastics_saved") val totalPlasticsSaved: Int = 0,
    @SerialName("report_type") val reportType: String = "OVERALL",
    @SerialName("student_id") val studentId: String? = null,
    @SerialName("student_name") val studentName: String? = null,
    @SerialName("period_start") val periodStart: Long? = null,
    @SerialName("period_end") val periodEnd: Long? = null
)

@Serializable
data class NewReport(
    val title: String,
    val notes: String? = null,
    @SerialName("created_by") val createdBy: String,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("total_submissions") val totalSubmissions: Int,
    @SerialName("approved_count") val approvedCount: Int,
    @SerialName("pending_count") val pendingCount: Int,
    @SerialName("rejected_count") val rejectedCount: Int,
    @SerialName("total_points_awarded") val totalPointsAwarded: Int,
    @SerialName("total_plastics_saved") val totalPlasticsSaved: Int,
    @SerialName("report_type") val reportType: String = "OVERALL",
    @SerialName("student_id") val studentId: String? = null,
    @SerialName("student_name") val studentName: String? = null,
    @SerialName("period_start") val periodStart: Long? = null,
    @SerialName("period_end") val periodEnd: Long? = null
)

@Serializable
data class ReportNotesUpdate(
    val title: String,
    val notes: String?
)
@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.AdminEntity
import com.example.project1.data.model.EcoSubmissionEntity
import com.example.project1.data.model.TaskEntity
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.ProfileStatChip
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.ui.theme.EcoColors

@Composable
internal fun StaffDirectoryPage(
    currentAdminId: String,
    staff: List<AdminEntity>,
    loading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onViewDetails: (AdminEntity) -> Unit
) {
    // State for filtering staff search results
    var query by remember { mutableStateOf("") }
    val filteredStaff = remember(staff, query) {
        if (query.isBlank()) staff else staff.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.adminId.contains(query, ignoreCase = true) ||
                    it.faculty.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Staff Directory", onBack = onBack) {
            IconButton(onClick = onRefresh, enabled = !loading) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EcoColors.PrimaryGreen)
            }
        }
        Text(
            "${staff.size} campus admin${if (staff.size == 1) "" else "s"} with access to the staff desk",
            fontSize = 12.sp,
            color = EcoColors.TextMuted
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it.withoutEmoji() },
            singleLine = true,
            placeholder = { Text("Search by name, ID or faculty") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display loading spinner, empty states, or filtered staff list
        when {
            loading -> Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EcoColors.PrimaryGreen)
            }
            staff.isEmpty() -> Text("No staff records found.", fontSize = 13.sp, color = EcoColors.TextMuted)
            filteredStaff.isEmpty() -> Text("No staff match your search.", fontSize = 13.sp, color = EcoColors.TextMuted)
            else -> Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                filteredStaff.forEach { colleague ->
                    ProfilePersonCard(
                        title = colleague.name + if (colleague.adminId == currentAdminId) " (You)" else "",
                        subtitle = "${colleague.adminId} · ${colleague.faculty.ifBlank { "Staff" }}",
                        caption = "Tap to view staff details",
                        avatarName = colleague.name,
                        highlight = colleague.adminId == currentAdminId,
                        showChevron = true,
                        onClick = { onViewDetails(colleague) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
internal fun StaffDetailsPage(
    staff: AdminEntity?,
    isYou: Boolean,
    submissions: List<EcoSubmissionEntity>,
    tasks: List<TaskEntity>,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Staff Details", onBack = onBack)

        if (staff == null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Staff member not found.", fontSize = 13.sp, color = EcoColors.TextMuted)
            return@Column
        }

        // Filter and calculate reviewer performance stats
        val reviewedSubmissions = remember(submissions, staff.adminId) {
            submissions.filter { it.reviewedBy == staff.adminId }
        }
        val reviewedTasks = remember(tasks, staff.adminId) {
            tasks.filter { it.reviewedBy == staff.adminId }
        }
        val totalReviewed = reviewedSubmissions.size + reviewedTasks.size
        val approvedReviewed = reviewedSubmissions.count { it.status.equals("Approved", ignoreCase = true) } +
                reviewedTasks.count { it.status.equals("Approved", ignoreCase = true) }
        val completionRate = if (totalReviewed == 0) 0 else (approvedReviewed * 100) / totalReviewed

        // Gather recent review activity items
        val recentActivity = remember(reviewedSubmissions, reviewedTasks) {
            (reviewedSubmissions.map { ProfileActivityItem.FromSubmission(it, it.reviewTimestamp ?: it.timestamp) } +
                    reviewedTasks.map { ProfileActivityItem.FromTask(it, it.reviewTimestamp ?: it.timestamp) })
                .sortedByDescending { it.timestamp }
                .take(6)
        }

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfilePhotoAvatar(name = staff.name, photoPath = null, color = EcoColors.DarkGreen, size = 56.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(staff.name + if (isYou) " (You)" else "", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = EcoColors.TextDark)
                    Text("Campus Admin · ${staff.faculty.ifBlank { "Staff" }}", fontSize = 12.sp, color = EcoColors.PrimaryGreen)
                }
            }

            ProfileSectionCard(title = "STAFF INFORMATION") {
                ProfileDetailRow(label = "Admin ID", value = staff.adminId)
                ProfileDetailRow(label = "Faculty", value = staff.faculty.ifBlank { "Staff" })
            }

            ProfileSectionCard(title = "PERFORMANCE") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    ProfileStatChip(Modifier.weight(1f), Icons.AutoMirrored.Filled.Assignment, "Submissions Marked", reviewedSubmissions.size.toString(), size = 7.sp)
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.TaskAlt, "Tasks Reviewed", reviewedTasks.size.toString())
                    ProfileStatChip(Modifier.weight(1f), Icons.Default.CheckCircle, "Approval Rate", if (totalReviewed == 0) "—" else "$completionRate%")
                }
            }

            ProfileSectionCard(title = "RECENT ACTIVITY") {
                if (recentActivity.isEmpty()) {
                    Text(
                        "No submissions or tasks reviewed by this staff member yet.",
                        fontSize = 12.sp,
                        color = EcoColors.TextMuted
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentActivity.forEach { activity -> ProfileActivityRow(activity) }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
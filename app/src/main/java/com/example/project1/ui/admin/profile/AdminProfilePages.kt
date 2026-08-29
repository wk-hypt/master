@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.admin.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.AdminEntity
import com.example.project1.ui.common.ProfileMenuRow
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.ProfileStatChip
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.ui.theme.EcoColors

@Composable
internal fun AdminHubPage(
    displayName: String,
    adminId: String,
    faculty: String,
    pendingSubmissionsCount: Int,
    pendingTasksCount: Int,
    totalStudents: Int,
    onOpenInfo: () -> Unit,
    onChangePassword: () -> Unit,
    onOpenStaffDirectory: () -> Unit,
    onOpenUserManagement: () -> Unit,
    onOpenPasswordResets: () -> Unit,
    pendingPasswordResetsCount: Int = 0,
    onOpenPendingQueue: (tab: Int) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(EcoColors.DarkGreen),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Staff Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("ECO TARUMT Control Desk", color = Color(0xFFC8E6C9), fontSize = 13.sp)
            }
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-28).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                ProfilePhotoAvatar(
                    name = displayName,
                    photoPath = null,
                    color = EcoColors.DarkGreen,
                    size = 56.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EcoColors.TextDark)
                    Text(adminId, fontSize = 13.sp, color = EcoColors.TextMuted)
                    Text(
                        "Campus Admin · ${faculty.ifBlank { "Staff" }}",
                        fontSize = 12.sp,
                        color = EcoColors.PrimaryGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-16).dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileStatChip(Modifier.weight(1f), Icons.AutoMirrored.Filled.Assignment, "Pending Subs", pendingSubmissionsCount.toString(), onClick = { onOpenPendingQueue(0) })
            ProfileStatChip(Modifier.weight(1f), Icons.Default.HourglassTop, "Pending Tasks", pendingTasksCount.toString(), onClick = { onOpenPendingQueue(1) })
            ProfileStatChip(Modifier.weight(1f), Icons.Default.Groups, "Students", totalStudents.toString(), onClick = onOpenUserManagement)
        }

        Card(
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                ProfileMenuRow("STAFF INFO", Icons.Default.Badge, onOpenInfo)
                ProfileMenuRow("CHANGE PASSWORD", Icons.Default.Lock, onChangePassword)
                ProfileMenuRow("STAFF DIRECTORY", Icons.Default.Group, onOpenStaffDirectory)
                ProfileMenuRow("USER MANAGEMENT", Icons.Default.ManageAccounts, onOpenUserManagement)
                ProfileMenuRow(
                    if (pendingPasswordResetsCount > 0) {
                        "PASSWORD RESETS ($pendingPasswordResetsCount)"
                    } else {
                        "PASSWORD RESETS"
                    },
                    Icons.Default.LockReset,
                    onOpenPasswordResets
                )
                ProfileMenuRow("LOG OUT", Icons.AutoMirrored.Filled.Logout, onLogout, tint = EcoColors.Danger)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
internal fun AdminInfoPage(
    admin: AdminEntity?,
    onBack: () -> Unit,
    onSave: (name: String, faculty: String) -> Unit
) {
    val originalName = admin?.name.orEmpty()
    val originalFaculty = admin?.faculty.orEmpty()
    var name by remember(admin?.adminId, originalName) { mutableStateOf(originalName) }
    var faculty by remember(admin?.adminId, originalFaculty) { mutableStateOf(originalFaculty) }
    val isNameBlank = name.isBlank()
    val hasChanges = name != originalName || faculty != originalFaculty

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Staff info", onBack = onBack)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = admin?.adminId.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Admin ID") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                focusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF424242),
                unfocusedLabelColor = Color(0xFF424242),
                unfocusedTrailingIconColor = Color(0xFF424242)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.withoutEmoji() },
            label = { Text("Name") },
            singleLine = true,
            isError = isNameBlank,
            supportingText = { if (isNameBlank) Text("Name cannot be empty", color = EcoColors.Danger) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                focusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF424242),
                unfocusedLabelColor = Color(0xFF424242),
                unfocusedTrailingIconColor = Color(0xFF424242)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = faculty,
            onValueChange = { faculty = it.withoutEmoji() },
            label = { Text("Faculty") },
            singleLine = true,
            supportingText = { Text("Defaults to \"FOCS\" if left blank") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EcoColors.PrimaryGreen,
                focusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedBorderColor = Color(0xFF424242),
                unfocusedLabelColor = Color(0xFF424242),
                unfocusedTrailingIconColor = Color(0xFF424242)
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (hasChanges) {
                OutlinedButton(
                    onClick = {
                        name = originalName
                        faculty = originalFaculty
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Discard") }
            }
            Button(
                onClick = { onSave(name.trim(), faculty.trim()) },
                enabled = hasChanges && !isNameBlank,
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) { Text(if (hasChanges) "Save changes" else "No changes") }
        }
    }
}

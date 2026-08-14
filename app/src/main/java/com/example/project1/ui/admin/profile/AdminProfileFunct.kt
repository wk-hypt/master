package com.example.project1.ui.admin.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.AdminEntity

private val PrimaryGreen = Color(0xFF2E7D32)
private val DarkGreen = Color(0xFF1B5E20)
private val PageBg = Color(0xFFF4F6F5)
private val SoftGreen = Color(0xFFE8F5E9)

private enum class AdminProfilePage { Hub, Info, Support, Faq, Contact, About }

@Composable
fun AdminProfileFunct(
    admin: AdminEntity?,
    onSaveStaffInfo: (name: String, faculty: String) -> Unit,
    onChangePassword: (current: String, newPassword: String, confirm: String) -> Unit,
    onLogout: () -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var page by remember { mutableStateOf(AdminProfilePage.Hub) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val displayName = admin?.name.orEmpty().ifBlank { "Staff" }
    val adminId = admin?.adminId.orEmpty()
    val faculty = admin?.faculty.orEmpty()

    Scaffold(
        modifier = modifier,
        containerColor = PageBg,
        snackbarHost = snackbarHost
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (page) {
                AdminProfilePage.Hub -> AdminHubPage(
                    displayName = displayName,
                    adminId = adminId,
                    faculty = faculty,
                    onOpenInfo = { page = AdminProfilePage.Info },
                    onChangePassword = { showPasswordDialog = true },
                    onOpenSupport = { page = AdminProfilePage.Support },
                    onLogout = { showLogoutConfirm = true }
                )
                AdminProfilePage.Info -> AdminInfoPage(
                    admin = admin,
                    onBack = { page = AdminProfilePage.Hub },
                    onSave = onSaveStaffInfo
                )
                AdminProfilePage.Support -> AdminSupportMenu(
                    onBack = { page = AdminProfilePage.Hub },
                    onFaq = { page = AdminProfilePage.Faq },
                    onContact = { page = AdminProfilePage.Contact },
                    onAbout = { page = AdminProfilePage.About }
                )
                AdminProfilePage.Faq -> AdminTextPage(
                    title = "FAQ",
                    onBack = { page = AdminProfilePage.Support },
                    blocks = listOf(
                        "How do I approve student submissions?" to
                            "Open the Approval tab, review the photo and details, then award points or reject with feedback.",
                        "Where can I add campus vouchers?" to
                            "Use the Rewards tab to create, edit, or remove vouchers in the campus catalog.",
                        "Where are campus stats?" to
                            "The Report tab shows submission volume, approval rate, and top contributors."
                    )
                )
                AdminProfilePage.Contact -> AdminTextPage(
                    title = "Contact Us",
                    onBack = { page = AdminProfilePage.Support },
                    blocks = listOf(
                        "Email" to "ecoapp.support@tarumt.edu.my",
                        "Office Hours" to "Monday - Friday, 9:00 AM - 5:00 PM",
                        "Location" to "TAR UMT Kuala Lumpur campus"
                    )
                )
                AdminProfilePage.About -> AdminTextPage(
                    title = "About Us",
                    onBack = { page = AdminProfilePage.Support },
                    blocks = listOf(
                        "ECO TARUMT Staff Desk" to
                            "Staff tools for verifying eco actions, managing campus rewards, and tracking SDG 12 impact across TAR UMT."
                    )
                )
            }
        }
    }

    if (showPasswordDialog) {
        AdminPasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { current, next, confirm ->
                onChangePassword(current, next, confirm)
                showPasswordDialog = false
            }
        )
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("Log out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of the staff desk?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) { Text("Log out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminHubPage(
    displayName: String,
    adminId: String,
    faculty: String,
    onOpenInfo: () -> Unit,
    onChangePassword: () -> Unit,
    onOpenSupport: () -> Unit,
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
                .background(DarkGreen),
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
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SoftGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = PrimaryGreen)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(displayName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B1F1C))
                    Text(adminId, fontSize = 13.sp, color = Color(0xFF6B7280))
                    Text(
                        "Campus Admin · ${faculty.ifBlank { "Staff" }}",
                        fontSize = 12.sp,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-12).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                AdminMenuRow("STAFF INFO", onOpenInfo)
                AdminMenuRow("CHANGE PASSWORD", onChangePassword)
                AdminMenuRow("SUPPORT", onOpenSupport)
                AdminMenuRow("LOG OUT", onLogout)
            }
        }
    }
}

@Composable
private fun AdminInfoPage(
    admin: AdminEntity?,
    onBack: () -> Unit,
    onSave: (name: String, faculty: String) -> Unit
) {
    var name by remember(admin?.adminId, admin?.name) { mutableStateOf(admin?.name.orEmpty()) }
    var faculty by remember(admin?.adminId, admin?.faculty) { mutableStateOf(admin?.faculty.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Staff info", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = admin?.adminId.orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("Admin ID") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = faculty,
            onValueChange = { faculty = it },
            label = { Text("Faculty") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSave(name, faculty) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Save changes")
        }
    }
}

@Composable
private fun AdminSupportMenu(
    onBack: () -> Unit,
    onFaq: () -> Unit,
    onContact: () -> Unit,
    onAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text("Support", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                AdminMenuRow("FAQ", onFaq)
                AdminMenuRow("CONTACT US", onContact)
                AdminMenuRow("ABOUT US", onAbout)
            }
        }
    }
}

@Composable
private fun AdminTextPage(
    title: String,
    onBack: () -> Unit,
    blocks: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
            }
            Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                blocks.forEach { (heading, body) ->
                    Text(heading, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(body, fontSize = 13.sp, color = Color(0xFF495057), lineHeight = 19.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminMenuRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2C2C2C))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF9E9E9E))
    }
}

@Composable
private fun AdminPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (current: String, newPassword: String, confirm: String) -> Unit
) {
    var current by remember { mutableStateOf("") }
    var next by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change password", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = current,
                    onValueChange = { current = it },
                    label = { Text("Current password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = next,
                    onValueChange = { next = it },
                    label = { Text("New password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirm new password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(current, next, confirm) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) { Text("Save") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

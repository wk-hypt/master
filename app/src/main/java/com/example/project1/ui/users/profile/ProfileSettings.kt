@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Support
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.ui.common.ProfileMenuRow
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.theme.EcoColors

@Composable
internal fun SettingsPage(
    notificationsEnabled: Boolean,
    onToggleNotifications: (Boolean) -> Unit,
    onBack: () -> Unit,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit,
    onFaq: () -> Unit,
    onContact: () -> Unit,
    onAbout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoColors.Cream)
            .padding(horizontal = 20.dp)
    ) {
        ProfilePageHeader(title = "Settings", onBack = onBack)
        Spacer(modifier = Modifier.height(8.dp))

        // preferences section containing settings switches
        SettingsGroup("Preferences") {
            ToggleRow("Notifications", Icons.Default.Notifications, notificationsEnabled, onToggleNotifications)
        }
        Spacer(modifier = Modifier.height(20.dp))
        // security section for password management and account deletion
        SettingsGroup("Account Security") {
            ProfileMenuRow("CHANGE PASSWORD", Icons.Default.Lock, onChangePassword)
            ProfileMenuRow("DELETE ACCOUNT", Icons.Default.Person, onDeleteAccount, tint = EcoColors.Danger)
        }
        Spacer(modifier = Modifier.height(20.dp))
        // support and information options
        SettingsGroup("Support") {
            ProfileMenuRow("FAQ", Icons.Default.Info, onFaq)
            ProfileMenuRow("CONTACT US", Icons.Default.Support, onContact)
            ProfileMenuRow("ABOUT US", Icons.Default.Info, onAbout)
        }
    }
}

@Composable
internal fun SupportTextPage(
    title: String,
    onBack: () -> Unit,
    blocks: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EcoColors.Cream)
            .padding(horizontal = 20.dp)
    ) {
        ProfilePageHeader(title = title, onBack = onBack)
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            // scrollable container rendering heading and body content blocks
            Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                blocks.forEach { (heading, body) ->
                    Column(modifier = Modifier.padding(bottom = 14.dp)) {
                        Text(heading, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = androidx.compose.ui.graphics.Color(0xFF212529))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(body, fontSize = 13.sp, color = androidx.compose.ui.graphics.Color(0xFF495057), lineHeight = 19.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
    ) {
        Column { content() }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = androidx.compose.ui.graphics.Color(0xFF2C2C2C))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = EcoColors.PrimaryGreen)
        )
    }
}
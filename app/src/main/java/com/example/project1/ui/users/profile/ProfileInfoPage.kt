@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

import android.app.DatePickerDialog
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.model.UserEntity
import com.example.project1.ui.common.AvatarPalette
import com.example.project1.ui.common.ProfileCameraBadge
import com.example.project1.ui.common.ProfileColors
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.launchImagePicker
import com.example.project1.ui.common.rememberImagePicker
import java.util.Calendar

private val TarUmtFaculties = listOf(
    "FAFB" to "Faculty of Accountancy, Finance and Business",
    "FOAS" to "Faculty of Applied Sciences",
    "FOCS" to "Faculty of Computing and Information Technology",
    "FOBE" to "Faculty of Built Environment",
    "FOET" to "Faculty of Engineering and Technology",
    "FCCI" to "Faculty of Communication and Creative Industries",
    "FSSH" to "Faculty of Social Science and Humanities"
)

private fun facultyDisplayName(code: String): String {
    val trimmed = code.trim()
    if (trimmed.isBlank()) return "Select your faculty"
    val match = TarUmtFaculties.firstOrNull { it.first.equals(trimmed, ignoreCase = true) }
    return if (match != null) "${match.first} - ${match.second}" else trimmed
}

private fun isValidEmail(email: String): Boolean =
    email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

private fun isValidPhone(phone: String): Boolean =
    phone.isBlank() || Regex("^[+]?[0-9 ()-]{7,15}$").matches(phone.trim())

@Composable
internal fun ProfileInfoPage(
    user: UserEntity?,
    avatarColor: Color,
    avatarColorIndex: Int,
    onAvatarColorSelected: (Int) -> Unit,
    profilePhotoPath: String?,
    onProfilePhotoPicked: (android.net.Uri) -> Unit,
    onRemoveProfilePhoto: () -> Unit,
    onBack: () -> Unit,
    onSave: (name: String, faculty: String, phone: String, email: String, birthday: String) -> Unit
) {
    val context = LocalContext.current
    val photoPicker = rememberImagePicker(onProfilePhotoPicked)
    var name by remember(user?.studentId, user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var faculty by remember(user?.studentId, user?.faculty) {
        mutableStateOf(user?.faculty.orEmpty().ifBlank { "FOCS" })
    }
    var phone by remember(user?.studentId, user?.phone) { mutableStateOf(user?.phone.orEmpty()) }
    var email by remember(user?.studentId, user?.email) { mutableStateOf(user?.email.orEmpty()) }
    var birthday by remember(user?.studentId, user?.birthday) { mutableStateOf(user?.birthday.orEmpty()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showFacultyPicker by remember { mutableStateOf(false) }
    var touched by remember { mutableStateOf(false) }

    val nameError = touched && name.isBlank()
    val emailError = touched && !isValidEmail(email)
    val phoneError = touched && !isValidPhone(phone)
    val canSave = name.isNotBlank() && isValidEmail(email) && isValidPhone(phone)

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Profile info", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                ProfilePhotoAvatar(
                    name = name.ifBlank { "S" },
                    photoPath = profilePhotoPath,
                    color = avatarColor,
                    size = 92.dp,
                    onClick = { launchImagePicker(photoPicker) }
                )
                Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                    ProfileCameraBadge { launchImagePicker(photoPicker) }
                }
                if (profilePhotoPath == null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2196F3))
                            .clickable { showColorPicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = "Change avatar color", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                if (profilePhotoPath != null) "Tap your photo to change it" else "Tap the camera to add a photo, or the palette to pick a color",
                fontSize = 11.sp,
                color = Color(0xFF9E9E9E),
                textAlign = TextAlign.Center
            )
            if (profilePhotoPath != null) {
                Text(
                    "Remove photo",
                    fontSize = 11.sp,
                    color = ProfileColors.Danger,
                    modifier = Modifier.padding(top = 2.dp).clickable(onClick = onRemoveProfilePhoto)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            ProfileField("Student ID", user?.studentId.orEmpty().ifBlank { "—" }, readOnly = true, enabled = false)
            ProfileField("Name", name, isError = nameError, supportingText = if (nameError) "Name cannot be empty" else null) { name = it }
            ProfileField("Phone No", phone, isError = phoneError, supportingText = if (phoneError) "Enter a valid phone number" else "Optional") { phone = it }
            ProfileField("Email", email, isError = emailError, supportingText = if (emailError) "Enter a valid email address" else "Optional") { email = it }
            ProfileField(
                label = "Birthday Date",
                value = birthday,
                readOnly = true,
                onClick = {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day -> birthday = "%02d/%02d/%04d".format(day, month + 1, year) },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).apply { datePicker.maxDate = System.currentTimeMillis() }.show()
                }
            )
            ProfileField("Faculty", facultyDisplayName(faculty), readOnly = true, onClick = { showFacultyPicker = true })

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    touched = true
                    if (canSave) onSave(name.trim(), faculty, phone.trim(), email.trim(), birthday.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = ProfileColors.PrimaryGreen),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save changes", color = Color.White, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showColorPicker && profilePhotoPath == null) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Choose avatar color", fontWeight = FontWeight.Bold) },
            text = {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(AvatarPalette) { index, color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (index == avatarColorIndex) 3.dp else 0.dp,
                                    color = ProfileColors.TextDark,
                                    shape = CircleShape
                                )
                                .clickable {
                                    onAvatarColorSelected(index)
                                    showColorPicker = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (index == avatarColorIndex) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showColorPicker = false }) { Text("Done") } }
        )
    }

    if (showFacultyPicker) {
        AlertDialog(
            onDismissRequest = { showFacultyPicker = false },
            title = { Text("Choose your faculty", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    TarUmtFaculties.forEach { (code, facultyName) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                faculty = code
                                showFacultyPicker = false
                            }.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = code.equals(faculty.trim(), ignoreCase = true),
                                onClick = {
                                    faculty = code
                                    showFacultyPicker = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = ProfileColors.PrimaryGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(code, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ProfileColors.TextDark)
                                Text(facultyName, fontSize = 11.sp, color = ProfileColors.TextGrey)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFacultyPicker = false }) { Text("Done") } }
        )
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    onClick: (() -> Unit)? = null,
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = ProfileColors.TextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                readOnly = readOnly || onClick != null,
                enabled = enabled && onClick == null,
                isError = isError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                supportingText = supportingText?.let { text ->
                    {
                        Text(
                            text,
                            fontSize = 11.sp,
                            color = if (isError) ProfileColors.Danger else ProfileColors.TextGrey
                        )
                    }
                }
            )
        }
    }
}
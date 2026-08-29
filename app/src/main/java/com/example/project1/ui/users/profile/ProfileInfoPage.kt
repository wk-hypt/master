@file:Suppress("SpellCheckingInspection")

package com.example.project1.ui.users.profile

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.ui.common.withoutEmoji
import com.example.project1.data.model.UserEntity
import com.example.project1.ui.common.ProfileCameraBadge
import com.example.project1.ui.common.ProfilePageHeader
import com.example.project1.ui.common.ProfilePhotoAvatar
import com.example.project1.ui.common.launchImagePicker
import com.example.project1.ui.common.rememberImagePicker
import com.example.project1.ui.theme.EcoColors
import java.util.Calendar
import java.util.TimeZone

// list of available TAR UMT faculties with their respective codes and full titles
private val TarUmtFaculties = listOf(
    "FAFB" to "Faculty of Accountancy, Finance and Business",
    "FOAS" to "Faculty of Applied Sciences",
    "FOCS" to "Faculty of Computing and Information Technology",
    "FOBE" to "Faculty of Built Environment",
    "FOET" to "Faculty of Engineering and Technology",
    "FCCI" to "Faculty of Communication and Creative Industries",
    "FSSH" to "Faculty of Social Science and Humanities"
)

// helper to retrieve full faculty name or return code as fallback
private fun facultyDisplayName(code: String): String {
    val trimmed = code.trim()
    if (trimmed.isBlank()) return "Select your faculty"
    val match = TarUmtFaculties.firstOrNull { it.first.equals(trimmed, ignoreCase = true) }
    return if (match != null) "${match.first} - ${match.second}" else trimmed
}

private val BirthdayUtc = TimeZone.getTimeZone("UTC")
private const val MinBirthdayYear = 1920

private fun utcCalendar(): Calendar = Calendar.getInstance(BirthdayUtc)

// convert date string (dd/MM/yyyy) to UTC milliseconds for date picker
private fun birthdayToMillis(birthday: String): Long? {
    val parts = birthday.trim().split("/")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    return utcCalendar().apply {
        clear()
        set(year, month - 1, day)
    }.timeInMillis
}

// convert UTC milliseconds to formatted date string (dd/MM/yyyy)
private fun millisToBirthday(millis: Long): String {
    val cal = utcCalendar().apply { timeInMillis = millis }
    return "%02d/%02d/%04d".format(
        cal.get(Calendar.DAY_OF_MONTH),
        cal.get(Calendar.MONTH) + 1,
        cal.get(Calendar.YEAR)
    )
}

// default date set to 18 years prior to current year
private fun defaultBirthdayMillis(): Long = utcCalendar().apply {
    add(Calendar.YEAR, -18)
}.timeInMillis

@OptIn(ExperimentalMaterial3Api::class)
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
    val photoPicker = rememberImagePicker(onProfilePhotoPicked)
    var name by remember(user?.studentId, user?.name) { mutableStateOf(user?.name.orEmpty()) }
    var faculty by remember(user?.studentId, user?.faculty) {
        mutableStateOf(user?.faculty.orEmpty().ifBlank { "FOCS" })
    }
    var phone by remember(user?.studentId, user?.phone) { mutableStateOf(user?.phone.orEmpty()) }
    var email by remember(user?.studentId, user?.email) { mutableStateOf(user?.email.orEmpty()) }
    var birthday by remember(user?.studentId, user?.birthday) { mutableStateOf(user?.birthday.orEmpty()) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showBirthdayPicker by remember { mutableStateOf(false) }
    var showFacultyPicker by remember { mutableStateOf(false) }
    var errorName by remember { mutableStateOf<String?>(null) }
    var errorEmail by remember { mutableStateOf<String?>(null) }
    var errorPhoneNumber by remember { mutableStateOf<String?>(null) }

    // form validation rules for user inputs
    fun validate(): Boolean {
        errorName = when {
            name.isBlank() -> "Name cannot be empty"
            name.trim().length < 3 -> "Name must consists at least 3 characters"
            !name.all { it.isLetter() || it.isWhitespace() } -> "Name can only consists character and space"
            else -> null
        }

        errorEmail = when {
            email.isBlank() -> null
            !email.contains("@") || !email.contains(".com") -> "Invalid email, did not consists @ and .com"
            else -> null
        }

        val phoneDigits = phone.filter { it.isDigit() }
        errorPhoneNumber = when {
            phone.isBlank() -> null
            phoneDigits.length > 11 || phoneDigits.length < 10 -> "Invalid Phone Number, it is not a phone number!"
            else -> null
        }

        return errorName == null && errorEmail == null && errorPhoneNumber == null
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ProfilePageHeader(title = "Profile info", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            // avatar container with badge and color palette trigger
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
                    color = EcoColors.Danger,
                    modifier = Modifier.padding(top = 2.dp).clickable(onClick = onRemoveProfilePhoto)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            // input fields for user profile details
            ProfileField(label = "Student ID", value = user?.studentId.orEmpty().ifBlank { "—" }, readOnly = true, enabled = false, headingIcon = Icons.Default.Badge)
            ProfileField(
                label = "Name",
                value = name,
                isError = errorName != null,
                supportingText = errorName,
                headingIcon = Icons.Default.Person,
                onValueChange = { name = it }
            )

            ProfileField(
                label = "Phone No",
                value = phone,
                holder = "e.g. 01234567890",
                isError = errorPhoneNumber != null,
                supportingText = errorPhoneNumber ?: "Optional",
                headingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() || it == '-' }
                    if (filtered.length <= 12) {
                        phone = filtered
                    }
                }
            )
            ProfileField(
                label = "Email",
                value = email,
                holder = "e.g. tarubt@gmail.com",
                isError = errorEmail != null,
                supportingText = errorEmail ?: "Optional",
                headingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { input ->
                    email = input.trim()
                }
            )
            ProfileField(
                label = "Birthday Date",
                value = birthday,
                readOnly = true,
                headingIcon = Icons.Default.Cake,
                trailingIcon = Icons.Default.ArrowDropUp,
                onClick = { showBirthdayPicker = true }
            )
            ProfileField(label = "Faculty", value = facultyDisplayName(faculty), readOnly = true, headingIcon = Icons.Default.School, trailingIcon = Icons.Default.ArrowDropUp, onClick = { showFacultyPicker = true })

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (validate()) onSave(name.trim(), faculty, phone.trim(), email.trim(), birthday.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Save changes", color = Color.White, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // dialog to pick custom avatar color when no photo is set
    if (showColorPicker && profilePhotoPath == null) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Choose avatar color", fontWeight = FontWeight.Bold) },
            text = {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(EcoColors.AvatarPalette) { index, color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (index == avatarColorIndex) 3.dp else 0.dp,
                                    color = EcoColors.TextDark,
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

    // material date picker dialog for selecting birth date
    if (showBirthdayPicker) {
        val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = birthdayToMillis(birthday) ?: defaultBirthdayMillis(),
            yearRange = MinBirthdayYear..currentYear,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis <= System.currentTimeMillis()

                override fun isSelectableYear(year: Int): Boolean =
                    year in MinBirthdayYear..currentYear
            }
        )
        DatePickerDialog(
            onDismissRequest = { showBirthdayPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { birthday = millisToBirthday(it) }
                        showBirthdayPicker = false
                    }
                ) {
                    Text("OK", color = EcoColors.PrimaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBirthdayPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // dialog to select university faculty from list
    if (showFacultyPicker) {
        AlertDialog(
            onDismissRequest = { showFacultyPicker = false },
            title = { Text("Choose your faculty", fontWeight = FontWeight.Bold, color = Color.Black) },
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
                                colors = RadioButtonDefaults.colors(selectedColor = EcoColors.PrimaryGreen)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(code, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EcoColors.TextDark)
                                Text(facultyName, fontSize = 11.sp, color = EcoColors.TextMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showFacultyPicker = false }) { Text("Done") } },
            containerColor = Color.White
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
    holder: String? = null,
    supportingText: String? = null,
    headingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClick: (() -> Unit)? = null,
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = EcoColors.TextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = { onValueChange(it.withoutEmoji()) },
                readOnly = readOnly || onClick != null,
                enabled = enabled && onClick == null,
                placeholder = holder?.let { holderText -> { Text(text = holderText) } },
                isError = isError,
                singleLine = true,
                keyboardOptions = keyboardOptions,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = headingIcon?.let { icon ->
                    { Icon(imageVector = icon, contentDescription = label, tint = Color(0xFF424242)) }
                },
                trailingIcon = trailingIcon?.let { icon ->
                    { Icon(imageVector = icon, contentDescription = "Select $label", tint = Color(0xFF424242)) }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color(0xFF424242),
                    unfocusedBorderColor = Color(0xFF424242),
                    unfocusedLabelColor = Color(0xFF424242),
                    unfocusedTrailingIconColor = Color(0xFF424242)
                ),
                shape = RoundedCornerShape(8.dp),
                supportingText = supportingText?.let { text ->
                    {
                        Text(
                            text,
                            fontSize = 11.sp,
                            color = if (isError) EcoColors.Danger else EcoColors.TextGrey
                        )
                    }
                }
            )
        }
    }
}
package com.example.project1.ui.users.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.project1.common.RequiredLabel
import com.example.project1.common.withoutEmoji
import com.example.project1.ui.adaptive.AdaptiveDialogSurface
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.project1.ui.theme.EcoColors

data class EcoLogSubmissionInput(
    val imagePath: String,
    val actionType: String,
    val stallName: String,
    val quantity: Int,
    val description: String,
    val location: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoUploadDialog(
    onDismiss: () -> Unit,
    onSubmit: (EcoLogSubmissionInput) -> Unit
) {
    val context = LocalContext.current

    var pending by remember { mutableStateOf<Uri?>(null) }
    var captured by remember { mutableStateOf<Uri?>(null) }

    var actionType by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var stallName by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var terms by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showReplacePhotoDialog by remember { mutableStateOf(false) }

    val actionTypeOptions = listOf(
        "Bring Own Food Container",
        "Bring Reusable Water Bottle",
        "Refuse Single-Use Plastic / Straw",
        "Bring Shopping Bag",
        "Recycle Plastic Bottle / Can"
    )

    val estimatedPoints = remember(actionType, quantity) {
        val basePoints = when (actionType) {
            "Bring Own Food Container" -> 20
            "Bring Reusable Water Bottle" -> 10
            "Refuse Single-Use Plastic / Straw" -> 10
            "Bring Shopping Bag" -> 10
            "Recycle Plastic Bottle / Can" -> 15
            else -> 0
        }
        basePoints * quantity
    }

    val currentTime = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            captured = pending
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            pending = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = createImageUri(context)
            pending = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val isFormValid = captured != null && actionType.isNotBlank() && stallName.isNotBlank() && terms
    val photoHeight = if (LocalAppWindowInfo.current.heightSize == HeightSize.Compact) 120.dp else 200.dp

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) captured = uri
    }

    fun openGallery() {
        imagePickerLauncher.launch("image/*")
    }

    AdaptiveDialogSurface(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxSize()) {

                TopAppBar(
                    title = {
                        Text(
                            "New Eco Log Submission",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = EcoColors.PrimaryGreen
                    )
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Submission Time", fontSize = 12.sp, color = Color.Gray)
                        Text(currentTime, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(photoHeight)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8F9FA))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (captured != null) {
                            AsyncImage(
                                model = captured,
                                contentDescription = "Captured photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            IconButton(
                                onClick = { captured = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(
                                        Color.Black.copy(alpha = 0.6f),
                                        RoundedCornerShape(50)
                                    )
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove Photo", tint = Color.White)
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No photo taking yet", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }

                    Button(
                        onClick = { launchCamera() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (captured == null) "Take Photo" else "Retake Photo")
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Or choose from Gallery",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = EcoColors.PrimaryGreen,
                            style = androidx.compose.ui.text.TextStyle(
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable {
                                if (captured != null) {
                                    showReplacePhotoDialog = true
                                } else {
                                    openGallery()
                                }
                            }
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = actionType,
                            onValueChange = {},
                            readOnly = true,
                            label = { RequiredLabel("Action Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
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
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            actionTypeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        actionType = option
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = stallName,
                        onValueChange = { stallName = it.withoutEmoji() },
                        label = { RequiredLabel("Stall Name") },
                        placeholder = { Text("e.g. Convenience Store Name") },
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

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it.withoutEmoji() },
                        label = { Text("Location (optional)") },
                        placeholder = { Text("e.g. Yum Yum Canteen") },
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Quantity", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("(pcs/set)", fontSize = 12.sp, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { if (quantity > 1) quantity-- },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                            }
                            Text(
                                text = quantity.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = Color.Black
                            )
                            OutlinedButton(
                                onClick = { quantity++ },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.Black
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it.withoutEmoji() },
                        label = { Text("Description (optional)") },
                        placeholder = { Text("Any additional notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
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

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EcoColors.ApprovedBg),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Estimated Reward:", fontSize = 14.sp, color = EcoColors.PrimaryGreen)
                            Text(
                                "+$estimatedPoints Eco Points",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoColors.PrimaryGreen
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = terms,
                            onCheckedChange = { terms = it },
                            colors = CheckboxDefaults.colors(checkedColor = EcoColors.PrimaryGreen)
                        )
                        Text(
                            text = "I agree to the ",
                            fontSize = 13.sp,
                            color = Color(0xFF495057)
                        )
                        Text(
                            text = "Terms & Conditions",
                            fontSize = 13.sp,
                            color = EcoColors.PrimaryGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showTermsDialog = true }
                        )
                    }

                    Button(
                        onClick = {
                            captured?.let { uri ->
                                onSubmit(
                                    EcoLogSubmissionInput(
                                        imagePath = uri.toString(),
                                        actionType = actionType,
                                        stallName = stallName,
                                        quantity = quantity,
                                        description = description,
                                        location = location
                                    )
                                )
                            }
                        },
                        enabled = isFormValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoColors.PrimaryGreen,
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("Submit", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
    }

    if (showReplacePhotoDialog) {
        AlertDialog(
            onDismissRequest = { showReplacePhotoDialog = false },
            title = { Text("Replace photo?", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text("You already have a photo. Choose a new one from the gallery?", color = Color.Black) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReplacePhotoDialog = false
                        openGallery()
                    }
                ) {
                    Text("Replace", color = EcoColors.PrimaryGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReplacePhotoDialog = false }) {
                    Text("Cancel", color = Color.Red)
                }
            },
            containerColor = Color.White
        )
    }

    if (showTermsDialog) {
        TermsAndConditionsDialog(onDismiss = { showTermsDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit) {
    AdaptiveDialogSurface(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = {
                        Text("Terms & Conditions", fontWeight = FontWeight.Bold, color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = EcoColors.PrimaryGreen
                    )
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Eco Log Submission Terms",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212529)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TermsSection(
                        title = "1. Accuracy of Submission",
                        body = "You confirm that the photo and details submitted accurately reflect a genuine eco-friendly action performed by you, such as using a reusable container or tumbler."
                    )
                    TermsSection(
                        title = "2. Point Awarding",
                        body = "Points awarded are determined solely by campus staff upon review. Submission of a log does not guarantee approval or a fixed number of points."
                    )
                    TermsSection(
                        title = "3. Data Usage",
                        body = "Photos and details submitted may be reviewed by TAR UMT staff for verification purposes and stored as part of your submission history."
                    )
                    TermsSection(
                        title = "4. Fraudulent Submissions",
                        body = "Submitting false, duplicate, or misleading information may result in rejection of the submission and possible suspension of your account's eligibility for future rewards."
                    )
                    TermsSection(
                        title = "5. Changes to Terms",
                        body = "These terms may be updated periodically. Continued use of the eco log submission feature constitutes acceptance of the current terms."
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
    }
}

fun createImageUri(context: Context): Uri {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imagesDir = File(context.getExternalFilesDir("Pictures"), "")
    if (!imagesDir.exists()) imagesDir.mkdirs()

    val imageFile = File(imagesDir, "eco_log_$timestamp.jpg")

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
}

@Composable
private fun TermsSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212529)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = body,
            fontSize = 13.sp,
            color = Color(0xFF495057),
            lineHeight = 19.sp
        )
    }
}

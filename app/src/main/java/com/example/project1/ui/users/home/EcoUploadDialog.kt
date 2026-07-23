package com.example.project1.ui.users.home

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage

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

    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }

    var actionType by remember { mutableStateOf("") }
    var stallName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf(1) }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var termsAccepted by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            capturedUri = pendingUri
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createImageUri(context)
            pendingUri = uri
            cameraLauncher.launch(uri)
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = createImageUri(context)
            pendingUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val isFormValid = capturedUri != null &&
            actionType.isNotBlank() &&
            stallName.isNotBlank() &&
            termsAccepted

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {

                TopAppBar(
                    title = { Text("New Eco Log Submission") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (capturedUri != null) {
                            AsyncImage(
                                model = capturedUri,
                                contentDescription = "Captured photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No photo yet", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { launchCamera() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (capturedUri == null) "Take Photo" else "Retake Photo")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = actionType,
                        onValueChange = { actionType = it },
                        label = { Text("Action Type") },
                        placeholder = { Text("e.g. Reusable Container / Tumbler") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = stallName,
                        onValueChange = { stallName = it },
                        label = { Text("Stall Name") },
                        placeholder = { Text("e.g. Canteen Stall 5") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location (optional)") },
                        placeholder = { Text("e.g. Block A Canteen") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Quantity", fontSize = 14.sp, color = Color(0xFF495057))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { if (quantity > 1) quantity-- }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease")
                            }
                            Text(
                                text = quantity.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = { quantity++ }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (optional)") },
                        placeholder = { Text("Any additional notes") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Terms & Conditions 勾选行
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { termsAccepted = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2E7D32))
                        )
                        Text(
                            text = "I agree to the ",
                            fontSize = 13.sp,
                            color = Color(0xFF495057)
                        )
                        Text(
                            text = "Terms & Conditions",
                            fontSize = 13.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showTermsDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            capturedUri?.let { uri ->
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
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Submit", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    if (showTermsDialog) {
        TermsAndConditionsDialog(onDismiss = { showTermsDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Terms & Conditions") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
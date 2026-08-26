package com.example.project1.ui.users.task

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
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
import coil.compose.AsyncImage
import com.example.project1.ui.adaptive.AdaptiveDialogSurface
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.users.home.createImageUri
import com.example.project1.ui.theme.EcoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitProofDialog(
    onDismiss: () -> Unit,
    onSubmit: (imagePath: String) -> Unit
) {
    val context = LocalContext.current
    var pendingUri by remember { mutableStateOf<Uri?>(null) }
    var capturedUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) capturedUri = pendingUri }

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
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val uri = createImageUri(context)
            pendingUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val photoHeight = if (LocalAppWindowInfo.current.heightSize == HeightSize.Compact) 140.dp else 240.dp

    AdaptiveDialogSurface(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Submit Proof") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(photoHeight)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (capturedUri != null) {
                            AsyncImage(
                                model = capturedUri,
                                contentDescription = "Proof photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Take a photo to prove your progress", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { launchCamera() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (capturedUri == null) "Take Photo" else "Retake Photo")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { capturedUri?.let { onSubmit(it.toString()) } },
                        enabled = capturedUri != null,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.Blue)
                    ) {
                        Text("Submit", fontWeight = FontWeight.Bold)
                    }
                }
            }
    }
}
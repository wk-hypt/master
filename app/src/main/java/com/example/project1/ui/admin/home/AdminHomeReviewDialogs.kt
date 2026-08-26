package com.example.project1.ui.admin.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project1.common.withoutEmoji
import com.example.project1.ui.theme.EcoColors

private val QuickPointOptions = listOf(10, 20, 50, 100)
private val QuickPlasticOptions = listOf(1, 5, 10, 20)

@Composable
private fun AwardQuickChips(options: List<Int>, selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { value ->
            val isSelected = selected == value.toString()
            Surface(
                onClick = { onSelect(value.toString()) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) EcoColors.PrimaryGreen else EcoColors.AdminBg,
                border = if (isSelected) null else BorderStroke(1.dp, EcoColors.CardBorder)
            ) {
                Text(
                    "$value",
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else EcoColors.TextGrey2,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovePointsDialog(
    title: String,
    studentId: String,
    subtitle: String,
    onDismiss: () -> Unit,
    onConfirm: (points: Int, plasticSaved: Int) -> Unit,
    initialPlasticSaved: Int = 0
) {
    var pointsInput by remember { mutableStateOf("") }
    var plasticInput by remember {
        mutableStateOf(if (initialPlasticSaved > 0) initialPlasticSaved.toString() else "")
    }
    val isValid = pointsInput.toIntOrNull()?.let { it > 0 } ?: false

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = EcoColors.Surface,
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(EcoColors.ApprovedBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EcoColors.PrimaryGreen, modifier = Modifier.size(34.dp))
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(EcoColors.AdminBg).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(studentId, size = 38.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(studentId, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                        Text(subtitle, fontSize = 11.sp, color = EcoColors.TextGrey2)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "POINTS TO AWARD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextGrey,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pointsInput,
                    onValueChange = { input -> pointsInput = input.filter { it.isDigit() }.take(5) },
                    placeholder = { Text("e.g. 100") },
                    leadingIcon = { Icon(Icons.Default.Stars, contentDescription = null, tint = EcoColors.PrimaryGreen) },
                    suffix = { Text("pts", color = EcoColors.TextGrey2) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoColors.PrimaryGreen,
                        cursorColor = EcoColors.PrimaryGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                AwardQuickChips(options = QuickPointOptions, selected = pointsInput, onSelect = { pointsInput = it })

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "PLASTIC SAVED TO AWARD",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoColors.TextGrey,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = plasticInput,
                    onValueChange = { input -> plasticInput = input.filter { it.isDigit() }.take(5) },
                    placeholder = { Text("e.g. 5") },
                    leadingIcon = { Icon(Icons.Default.Recycling, contentDescription = null, tint = EcoColors.PrimaryGreen) },
                    suffix = { Text("items", color = EcoColors.TextGrey2) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EcoColors.PrimaryGreen,
                        cursorColor = EcoColors.PrimaryGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                AwardQuickChips(options = QuickPlasticOptions, selected = plasticInput, onSelect = { plasticInput = it })

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, EcoColors.CardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EcoColors.TextGrey2),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Cancel", fontWeight = FontWeight.Medium) }
                    Button(
                        onClick = {
                            val points = pointsInput.toIntOrNull() ?: return@Button
                            val plasticSaved = plasticInput.toIntOrNull() ?: 0
                            onConfirm(points, plasticSaved)
                        },
                        enabled = isValid,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EcoColors.PrimaryGreen),
                        modifier = Modifier.weight(1f).height(50.dp)
                    ) { Text("Confirm", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectFeedbackDialog(studentId: String, onDismiss: () -> Unit, onConfirm: (feedback: String) -> Unit) {
    var feedbackInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EcoColors.Surface,
        titleContentColor = EcoColors.TextDark,
        textContentColor = Color(0xFF495057),
        shape = RoundedCornerShape(20.dp),
        title = { DialogBadgeTitle("Reject Submission", "\u2715", EcoColors.RejectedBg, EcoColors.Rejected) },
        text = {
            Column {
                Text("Student: $studentId", fontSize = 13.sp, color = EcoColors.TextGrey2)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = feedbackInput,
                    onValueChange = { feedbackInput = it.withoutEmoji() },
                    label = { Text("Reason for rejection") },
                    placeholder = { Text("e.g. Proof incomplete, please resubmit") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(feedbackInput) },
                enabled = feedbackInput.isNotBlank(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EcoColors.Rejected)
            ) { Text("Confirm Reject") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) { Text("Cancel") }
        }
    )
}

@Composable
private fun DialogBadgeTitle(text: String, symbol: String, bg: Color, fg: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(bg), contentAlignment = Alignment.Center) {
            Text(symbol, color = fg, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 17.sp)
    }
}

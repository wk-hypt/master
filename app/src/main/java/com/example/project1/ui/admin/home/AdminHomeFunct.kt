package com.example.project1.ui.admin.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.entity.EcoSubmissionEntity

@Composable
fun AdminHomeFunct(
    pendingList: List<EcoSubmissionEntity>,
    onLogout: () -> Unit,
    onApproveClick: (Int, String) -> Unit,
    onRejectClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Staff Control Desk",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212529)
                )
                Text(
                    text = "SDG 12 Verification Portal",
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )
            }
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF495057)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Log Out")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9ECEF))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pending Actions Queue",
                    fontSize = 14.sp,
                    color = Color(0xFF495057),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${pendingList.size} submissions awaiting review",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (pendingList.isNotEmpty()) Color(0xFFD90429) else Color(0xFF2E7D32)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (pendingList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✨ All caught up! No pending reviews.",
                    color = Color(0xFF6C757D),
                    fontSize = 15.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendingList) { submission ->
                    PendingCardItem(
                        submission = submission,
                        onApprove = { onApproveClick(submission.id, submission.userId) },
                        onReject = { onRejectClick(submission.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PendingCardItem(
    submission: EcoSubmissionEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "StudentID: ${submission.userId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF212529)
                )
                Surface(
                    color = Color(0xFFFFF3CD),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = submission.status,
                        color = Color(0xFF856404),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Action: ${submission.actionType}", fontSize = 14.sp, color = Color(0xFF495057))
            Text(text = "Location: ${submission.stallName}", fontSize = 14.sp, color = Color(0xFF495057))

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFF1F3F5), shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📸 [ Image Verification Window ]", color = Color(0xFF6C757D), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(text = "File: ${submission.imagePath}", color = Color(0xFFADB5BD), fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC3545)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Reject")
                }

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF198754)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Approve (+100 Pts)")
                }
            }
        }
    }
}
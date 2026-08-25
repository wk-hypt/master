package com.example.project1.ui.users.rewards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.project1.common.generateQrBitmap
import com.example.project1.data.model.VoucherEntity

private val PrimaryGreen = Color(0xFF2E7D32)

@Composable
fun VoucherQrDialog(
    voucher: VoucherEntity,
    onDismiss: () -> Unit
) {
    val payload = voucher.qrCodePayload.orEmpty()
    val qrBitmap = remember(payload) {
        payload.takeIf { it.isNotBlank() }?.let { generateQrBitmap(it) }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = voucher.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B1F1C), textAlign = TextAlign.Center)
                Text(text = voucher.merchantName, fontSize = 13.sp, color = Color(0xFF6B7280))

                Spacer(modifier = Modifier.height(16.dp))

                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Voucher QR code",
                        modifier = Modifier.size(240.dp).background(Color.White, RoundedCornerShape(12.dp))
                    )
                } else {
                    Text(text = "QR code is not available for this voucher.", color = Color(0xFFC62828), fontSize = 13.sp, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Show this QR to staff when you use the voucher.", fontSize = 12.sp, color = Color(0xFF8B948E), textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

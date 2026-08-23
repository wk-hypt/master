package com.example.project1.ui.admin.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.data.model.VoucherEntity

private val PrimaryGreen = Color(0xFF2E7D32)
private val SoftExpiredBg = Color(0xFFFFEBEE)
private val PageBg = Color(0xFFF4F6F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRewardsFunct(
    available: List<VoucherEntity>,
    expired: List<VoucherEntity>,
    onAddClick: () -> Unit,
    onEditClick: (VoucherEntity) -> Unit,
    onDeleteClick: (VoucherEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        containerColor = PageBg,
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = PrimaryGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add voucher")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Rewards Catalog",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1F1C)
                )
                Text(
                    text = "Manage campus vouchers and expirations",
                    fontSize = 13.sp,
                    color = Color(0xFF8B948E)
                )
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = PrimaryGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Available (${available.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Expired (${expired.size})") }
                )
            }

            when (selectedTab) {
                0 -> AvailableAdminList(
                    vouchers = available,
                    onEditClick = onEditClick,
                    onDeleteClick = onDeleteClick
                )
                else -> ExpiredAdminList(
                    vouchers = expired,
                    onDeleteClick = onDeleteClick
                )
            }
        }
    }
}

@Composable
private fun AvailableAdminList(
    vouchers: List<VoucherEntity>,
    onEditClick: (VoucherEntity) -> Unit,
    onDeleteClick: (VoucherEntity) -> Unit
) {
    if (vouchers.isEmpty()) {
        EmptyState("No available vouchers. Tap + to create one.")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(vouchers, key = { it.id ?: it.title }) { voucher ->
            AdminAvailableCard(
                voucher = voucher,
                onEditClick = { onEditClick(voucher) },
                onDeleteClick = { onDeleteClick(voucher) }
            )
        }
    }
}

@Composable
private fun ExpiredAdminList(
    vouchers: List<VoucherEntity>,
    onDeleteClick: (VoucherEntity) -> Unit
) {
    if (vouchers.isEmpty()) {
        EmptyState("No expired vouchers found")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(vouchers, key = { it.id ?: it.title }) { voucher ->
            AdminExpiredCard(
                voucher = voucher,
                onDeleteClick = { onDeleteClick(voucher) }
            )
        }
    }
}

@Composable
private fun AdminAvailableCard(
    voucher: VoucherEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!voucher.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = voucher.imageUrl,
                    contentDescription = voucher.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voucher.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1B1F1C)
                )
                Text(
                    text = voucher.merchantName,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${voucher.pointsCost} pts · ${voucher.category}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryGreen
                    )
                    Text(
                        text = "Stock: ${voucher.quantity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (voucher.quantity > 0) Color(0xFF1565C0) else Color(0xFFC62828)
                    )
                }

                // Display Expiry Date if set
                if (!voucher.expiryDate.isNullOrBlank()) {
                    Text(
                        text = "Expires: ${voucher.expiryDate.take(10)}",
                        fontSize = 11.sp,
                        color = Color(0xFFD97706)
                    )
                }
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryGreen)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFC62828))
            }
        }
    }
}

@Composable
private fun AdminExpiredCard(
    voucher: VoucherEntity,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SoftExpiredBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = voucher.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1B1F1C)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = voucher.merchantName,
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Status: Expired",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828)
                )
                if (!voucher.expiryDate.isNullOrBlank()) {
                    Text(
                        text = "Expired on: ${voucher.expiryDate.take(10)}",
                        fontSize = 11.sp,
                        color = Color(0xFFC62828)
                    )
                }
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Expired", tint = Color(0xFFC62828))
            }
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color(0xFF8B948E), fontSize = 14.sp)
    }
}
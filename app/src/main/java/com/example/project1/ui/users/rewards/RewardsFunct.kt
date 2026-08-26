package com.example.project1.ui.users.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.project1.data.model.VoucherRules
import com.example.project1.ui.adaptive.HeightSize
import com.example.project1.ui.adaptive.LocalAppWindowInfo
import com.example.project1.ui.theme.EcoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsFunct(
    points: Int,
    available: List<VoucherEntity>,
    wallet: List<VoucherEntity>,
    onRedeem: (VoucherEntity) -> Unit,
    snackbarHost: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        containerColor = EcoColors.PageBg,
        snackbarHost = snackbarHost
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(if (LocalAppWindowInfo.current.heightSize == HeightSize.Compact) 12.dp else 20.dp)) {
                Text(text = "Rewards", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = EcoColors.TextDark)
                Text(text = "Spend eco points on campus vouchers", fontSize = 13.sp, color = Color(0xFF8B948E))

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = EcoColors.MintGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = EcoColors.PrimaryGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Your points", fontSize = 12.sp, color = Color(0xFF6B7280))
                            Text(text = "$points", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EcoColors.PrimaryGreen)
                        }
                    }
                }
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = EcoColors.PrimaryGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EcoColors.PrimaryGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Market (${available.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("My Wallet (${wallet.size})") }
                )
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (selectedTab) {
                0 -> MarketList(
                    vouchers = available,
                    points = points,
                    heldCounts = VoucherRules.heldCountByTitle(wallet),
                    onRedeem = onRedeem
                )
                else -> {
                    var qrVoucher by remember { mutableStateOf<VoucherEntity?>(null) }
                    LaunchedEffect(wallet, qrVoucher?.id) {
                        val openVoucher = qrVoucher ?: return@LaunchedEffect
                        val stillInWallet = wallet.any { it.id == openVoucher.id }
                        if (!stillInWallet) {
                            qrVoucher = null
                        }
                    }
                    WalletList(
                        redeemedVouchers = wallet,
                        onVoucherClick = { qrVoucher = it }
                    )
                    qrVoucher?.let { voucher ->
                        VoucherQrDialog(
                            voucher = voucher,
                            onDismiss = { qrVoucher = null }
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun MarketList(
    vouchers: List<VoucherEntity>,
    points: Int,
    heldCounts: Map<String, Int>,
    onRedeem: (VoucherEntity) -> Unit
) {
    if (vouchers.isEmpty()) {
        EmptyState(text = "No vouchers available right now")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(vouchers, key = { it.id ?: it.title }) { voucher ->
            val heldCount = heldCounts[voucher.title] ?: 0
            val atHoldLimit = VoucherRules.isAtHoldLimit(heldCount)
            MarketVoucherCard(
                voucher = voucher,
                heldCount = heldCount,
                atHoldLimit = atHoldLimit,
                canRedeem = points >= voucher.pointsCost && voucher.quantity > 0 && !atHoldLimit,
                onRedeemClick = { onRedeem(voucher) }
            )
        }
    }
}

@Composable
private fun MarketVoucherCard(
    voucher: VoucherEntity,
    heldCount: Int,
    atHoldLimit: Boolean,
    canRedeem: Boolean,
    onRedeemClick: () -> Unit
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
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(EcoColors.MintGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = EcoColors.PrimaryGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = voucher.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EcoColors.TextDark)
                Text(text = voucher.merchantName, fontSize = 12.sp, color = Color(0xFF6B7280))
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${voucher.pointsCost} pts · ${voucher.category}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = EcoColors.PrimaryGreen
                    )
                    Text(
                        text = if (voucher.quantity > 0) "Stock: ${voucher.quantity}" else "Out of Stock",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (voucher.quantity > 0) EcoColors.Blue else EcoColors.Danger
                    )
                    Text(
                        text = if (atHoldLimit) "Limit ($heldCount/${VoucherRules.MAX_HELD_PER_TYPE})"
                        else "Held: $heldCount/${VoucherRules.MAX_HELD_PER_TYPE}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (atHoldLimit) EcoColors.Danger else EcoColors.PrimaryGreen
                    )
                }
            }

            Button(
                onClick = onRedeemClick,
                enabled = canRedeem,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoColors.PrimaryGreen,
                    disabledContainerColor = Color(0xFFE0E0E0),
                    disabledContentColor = if (voucher.quantity <= 0 || atHoldLimit) EcoColors.Danger else Color(0xFF9E9E9E)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = when {
                        voucher.quantity <= 0 -> "Sold Out"
                        atHoldLimit -> "Limit"
                        else -> "Redeem"
                    },
                    fontSize = 12.sp,
                    fontWeight = if (voucher.quantity <= 0 || atHoldLimit) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun WalletList(
    redeemedVouchers: List<VoucherEntity>,
    onVoucherClick: (VoucherEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (redeemedVouchers.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No redeemed vouchers in your wallet yet.", color = Color(0xFF8B948E), fontSize = 14.sp)
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxSize()
            .background(EcoColors.PageBg)
    ) {
        items(redeemedVouchers, key = { it.id ?: it.qrCodePayload.orEmpty() }) { voucher ->
            WalletVoucherCard(
                voucher = voucher,
                onClick = { onVoucherClick(voucher) }
            )
        }
    }
}

@Composable
private fun WalletVoucherCard(
    voucher: VoucherEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!voucher.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = voucher.imageUrl,
                        contentDescription = voucher.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EcoColors.MintGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = EcoColors.PrimaryGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = voucher.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EcoColors.TextDark)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = voucher.merchantName, fontSize = 12.sp, color = Color(0xFF6B7280))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "${voucher.pointsCost} pts · ${voucher.category}", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EcoColors.PrimaryGreen)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(EcoColors.MintGreen)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EcoColors.PrimaryGreen,
                        modifier = Modifier.size(14.dp))
                    Text(text = "Redeemed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoColors.PrimaryGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Tap to show QR", fontSize = 10.sp, color = Color(0xFF8B948E))
                    Text(text = "Show this to staff to use the voucher", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = EcoColors.TextDark)
                }

                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "Show QR code",
                    tint = EcoColors.PrimaryGreen,
                    modifier = Modifier.size(28.dp)
                )
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
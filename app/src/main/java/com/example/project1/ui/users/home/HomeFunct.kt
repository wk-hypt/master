package com.example.project1.ui.users.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.R
import com.example.project1.data.entity.*
import kotlinx.coroutines.delay

@Composable
fun HomeFunct(
    currentPoints: Int,
    totalPlasticSaved: Int,
    onUploadClick: () -> Unit,
    onFeatureClick: (String) -> Unit,
    onNavigateToRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayBanners = listOf(
        EcoBannerEntity(id = 1, imageUrl = "logo"),
        EcoBannerEntity(id = 2, imageUrl = "zero_plastic"),
        EcoBannerEntity(id = 3, imageUrl = "green_bazaar"),
        EcoBannerEntity(id = 4, imageUrl = "eco_recycling"),
        EcoBannerEntity(id = 5, imageUrl = "green_campus"),
    )

    val displayFeatures = listOf(
        EcoFeatureEntity(id = 1, imageUrl = "leaderbroad", title = "Check Your Ranking", bgColorHex = "#E91E63", targetRoute = "leaderboard"),
        EcoFeatureEntity(id = 2, imageUrl = "rewards", title = "Redeem Rewards", bgColorHex = "#1565C0", targetRoute = "rewards"),
        EcoFeatureEntity(id = 3, imageUrl = "history", title = "View Your History", bgColorHex = "#F9A825", targetRoute = "profile")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        EcoBannerSlider(banners = displayBanners)
        Spacer(modifier = Modifier.height(16.dp))
        EcoStatsDashboard(points = currentPoints, plasticSaved = totalPlasticSaved)
        Spacer(modifier = Modifier.height(16.dp))
        EcoUploadArea(onUploadClick = onUploadClick)
        Spacer(modifier = Modifier.height(16.dp))
        EcoFeatureGrid(features = displayFeatures, onFeatureClick = onFeatureClick)
        Spacer(modifier = Modifier.height(16.dp))
        HotRewardsMarket(onNavigateToRewards = onNavigateToRewards)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class RewardItem(
    val title: String,
    val cost: String,
    val description: String
)

@Composable
fun EcoStatsDashboard(points: Int, plasticSaved: Int, modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Eco Points", "Plastic Saved")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9F7))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF2E7D32)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF2E7D32) else Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedTab == 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$points",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF2E7D32)
                        )
                        Text(text = "Available Coins to Redeem", fontSize = 12.sp, color = Color.Gray)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${plasticSaved}g",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1565C0)
                        )
                        Text(text = "Total Plastic Waste Prevented", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun EcoUploadArea(onUploadClick: () -> Unit, modifier: Modifier = Modifier) {
    val stroke = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFAFAFA))
            .drawBehind {
                drawRoundRect(
                    color = Color(0xFFB0BEC5),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = stroke
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
            .clickable { onUploadClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Upload Icon",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap to upload eco log submission",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EcoBannerSlider(
    banners: List<EcoBannerEntity>,
    autoScrollDelayMillis: Long = 4000L,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(banners.size) {
        while (true) {
            delay(autoScrollDelayMillis)
            if (banners.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        HorizontalPager(state = pagerState) { page ->
            val banner = banners[page]

            val imageRes = when (banner.imageUrl) {
                "logo" -> R.drawable.banner5
                "zero_plastic" -> R.drawable.banner1
                "green_bazaar" -> R.drawable.banner2
                "eco_recycling" -> R.drawable.banner3
                "green_campus" -> R.drawable.banner4
                else -> 0
            }

            if (imageRes != 0) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Eco banner image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (page % 2 == 0) Color(0xFF2E7D32) else Color(0xFF1B5E20))
                )
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(banners.size) { index ->
                val color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f)
                Box(modifier = Modifier.size(6.dp).background(color, RoundedCornerShape(3.dp)))
            }
        }
    }
}

@Composable
fun EcoFeatureGrid(
    features: List<EcoFeatureEntity>,
    onFeatureClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            text = "More Information",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1565C0),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(features) { feature ->
                EcoFeatureTile(
                    feature = feature,
                    onClick = { onFeatureClick(feature.targetRoute) }
                )
            }
        }
    }
}

@Composable
fun EcoFeatureTile(
    feature: EcoFeatureEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageRes = when (feature.imageUrl) {
        "leaderbroad" -> R.drawable.feature1
        "rewards" -> R.drawable.feature2
        "history" -> R.drawable.feature3
        else -> 0
    }
    val featureColor = Color(android.graphics.Color.parseColor(feature.bgColorHex))

    Column(
        modifier = modifier
            .width(120.dp)
            .padding(5.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(featureColor),
            contentAlignment = Alignment.Center
        ) {
            if (imageRes != 0) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = feature.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    text = feature.title.take(1),
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(featureColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = feature.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = featureColor,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun HotRewardsMarket(
    onNavigateToRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        RewardItem(
            title = "TAR UMT Canteen RM2 Voucher",
            cost = "50 Coins",
            description = "Redeem this voucher at any campus canteen counter for RM2 off your total bill. Valid for one-time use only and cannot be combined with other promotions."
        ),
        RewardItem(
            title = "Eco Coffee 10% Off Coupon",
            cost = "100 Coins",
            description = "Enjoy 10% off any drink at Eco Coffee outlets within campus. Show this coupon at checkout before payment is made."
        ),
        RewardItem(
            title = "Campus Bookstore RM5 Discount",
            cost = "150 Coins",
            description = "Get RM5 off your next purchase at the campus bookstore. Applicable to stationery, textbooks, and merchandise."
        )
    )

    var selectedItem by remember { mutableStateOf<RewardItem?>(null) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Hot Rewards Market",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items.forEach { item ->
                RewardCard(
                    item = item,
                    onClick = { selectedItem = item }
                )
            }
        }
    }

    selectedItem?.let { item ->
        RewardDetailDialog(
            item = item,
            onDismiss = { selectedItem = null },
            onGoToRewards = {
                selectedItem = null
                onNavigateToRewards()
            }
        )
    }
}

@Composable
fun RewardCard(
    item: RewardItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2E7D32)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CardGiftcard,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.cost,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = Color(0xFFADB5BD)
            )
        }
    }
}

@Composable
fun RewardDetailDialog(
    item: RewardItem,
    onDismiss: () -> Unit,
    onGoToRewards: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF212529),
        textContentColor = Color(0xFF495057),
        title = {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.cost,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = Color(0xFF495057),
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onGoToRewards,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Go to Rewards")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
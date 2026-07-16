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
    banners: List<EcoBannerEntity>,
    features: List<EcoFeatureEntity>,
    submissions: List<EcoSubmissionEntity>,
    currentPoints: Int,
    totalPlasticSaved: Int,
    onUploadClick: () -> Unit,
    onFeatureClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val displayBanners = if (banners.isEmpty()) {
        listOf(
            EcoBannerEntity(id = 1, imageUrl = "logo"),
            EcoBannerEntity(id = 2, imageUrl = "zero_plastic"),
            EcoBannerEntity(id = 3, imageUrl = "green_bazaar"),
            EcoBannerEntity(id = 4, imageUrl = "eco_recycling"),
            EcoBannerEntity(id = 5, imageUrl = "green_campus"),
        )
    } else banners

    val displayFeatures = if (features.isEmpty()) {
        listOf(
            EcoFeatureEntity(id = 1, imageUrl = "leaderbroad", title = "Check Your Ranking", bgColorHex = "#E91E63", targetRoute = "leaderboard"),
            EcoFeatureEntity(id = 2, imageUrl = "rewards", title = "Redeem Rewards", bgColorHex = "#1565C0", targetRoute = "rewards"),
            EcoFeatureEntity(id = 3, imageUrl = "history", title = "View Your History", bgColorHex = "#F9A825", targetRoute = "profile")
        )
    } else features

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

        HotRewardsMarket()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

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
fun HotRewardsMarket(modifier: Modifier = Modifier) {
    val items = listOf(
        Pair("TAR UMT Canteen RM2 Voucher", "50 Coins"),
        Pair("Eco Coffee 10% Off Coupon", "100 Coins"),
        Pair("Campus Bookstore RM5 Discount", "150 Coins")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Hot Rewards Market",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .width(160.dp)
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.first,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 2
                        )
                        Text(
                            text = item.second,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}
package com.example.project1.ui.users.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.entity.*

@Composable
fun HomeFunct(
    banners: List<EcoBannerEntity>,
    features: List<EcoFeatureEntity>,
    submissions: List<EcoSubmissionEntity>,
    currentPoints: Int,
    totalPlasticSaved: Int,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayBanners = if (banners.isEmpty()) {
        listOf(
            EcoBannerEntity(id = 1, imageUrl = "", title = "Campus Eco Challenge", subtitle = "Bring your own container & save points", targetRoute = "rewards"),
            EcoBannerEntity(id = 2, imageUrl = "", title = "Weekly Reset Leaderboard", subtitle = "Check who is top this week!", targetRoute = "leaderboard")
        )
    } else banners

    val displayFeatures = if (features.isEmpty()) {
        listOf(
            EcoFeatureEntity(id = 1, title = "Lunchbox Log", subtitle = "Snap container", bgColorHex = "#2E7D32"),
            EcoFeatureEntity(id = 2, title = "Tumbler Log", subtitle = "Snap bottle", bgColorHex = "#1565C0")
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
        EcoFeatureGrid(features = displayFeatures)
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
            .height(110.dp)
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
fun EcoBannerSlider(banners: List<EcoBannerEntity>, modifier: Modifier = Modifier) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Box(modifier = modifier.fillMaxWidth().height(180.dp)) {
        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (page == 0) Color(0xFF2E7D32) else Color(0xFF1B5E20)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(banners[page].title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(banners[page].subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
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
fun EcoFeatureGrid(features: List<EcoFeatureEntity>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { feature ->
            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(android.graphics.Color.parseColor(feature.bgColorHex))
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = feature.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (feature.subtitle.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = feature.subtitle,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}


package com.example.project1.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.*

@Composable
fun HomeFunct(
    banners: List<EcoBannerEntity>,
    features: List<EcoFeatureEntity>,
    submissions: List<EcoSubmissionEntity>,
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

    val displaySubmissions = if (submissions.isEmpty()) {
        listOf(
            EcoSubmissionEntity(id = 1, userId = "2400123", actionType = "Tumbler", stallName = "Stall 3 Café", imagePath = "", status = "Approved"),
            EcoSubmissionEntity(id = 2, userId = "2400123", actionType = "Lunchbox", stallName = "Stall 5 Rice", imagePath = "", status = "Pending")
        )
    } else submissions

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        EcoBannerSlider(banners = displayBanners)
        Spacer(modifier = Modifier.height(16.dp))
        EcoFeatureGrid(features = displayFeatures)
        Spacer(modifier = Modifier.height(16.dp))
        EcoSubmissionSection(submissions = displaySubmissions)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EcoBannerSlider(
    banners: List<EcoBannerEntity>,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Box(modifier = modifier.fillMaxWidth().height(200.dp)) {
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
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { feature ->
            Card(
                modifier = Modifier.weight(1f).height(100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(android.graphics.Color.parseColor(feature.bgColorHex))
                )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = feature.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    if (feature.subtitle.isNotEmpty()) {
                        Text(text = feature.subtitle, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

@Composable
fun EcoSubmissionSection(
    submissions: List<EcoSubmissionEntity>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Recent History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "More", fontSize = 12.sp, color = Color.Gray)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(submissions) { submission ->
                EcoSubmissionCard(submission = submission)
            }
        }
    }
}

@Composable
fun EcoSubmissionCard(submission: EcoSubmissionEntity) {
    val statusColor = when (submission.status) {
        "Approved" -> Color(0xFFE8F5E9)
        "Rejected" -> Color(0xFFFFEBEE)
        else -> Color(0xFFFFF3E0)
    }
    val statusTextColor = when (submission.status) {
        "Approved" -> Color(0xFF2E7D32)
        "Rejected" -> Color(0xFFC62828)
        else -> Color(0xFFEF6C00)
    }

    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.LightGray)) {
                Box(
                    modifier = Modifier
                        .background(statusColor, RoundedCornerShape(bottomEnd = 8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = submission.status, color = statusTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = submission.actionType, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = submission.stallName, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }
        }
    }
}
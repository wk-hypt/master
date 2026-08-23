package com.example.project1.ui.users.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.project1.R
import com.example.project1.data.model.BannerItem
import com.example.project1.data.model.CampusVoucher
import com.example.project1.data.model.FeatureCardItem
import com.example.project1.data.model.VoucherRules
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun HomeFunct(
    supabaseClient: SupabaseClient,
    currentUserId: String,
    currentPoints: Int,
    totalPlasticSaved: Int,
    banners: List<BannerItem>,
    features: List<FeatureCardItem>,
    onUploadClick: () -> Unit,
    onFeatureClick: (String) -> Unit,
    onNavigateToRewards: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EcoBannerSlider(banners = banners)
        EcoStatsDashboard(points = currentPoints, plasticSaved = totalPlasticSaved)
        EcoUploadArea(onUploadClick = onUploadClick)
        EcoFeatureGrid(features = features, onFeatureClick = onFeatureClick)
        HotRewardsMarket(
            supabaseClient = supabaseClient,
            currentUserId = currentUserId,
            onNavigateToRewards = onNavigateToRewards
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun EcoStatsDashboard(points: Int, plasticSaved: Int, modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) } // default first tab (Eco Points)
    val tabs = listOf("Eco Points", "Plastic Saved") //tab name

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
                        Text(text = "Total Points u holding", fontSize = 12.sp, color = Color.Gray)
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
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) // the dotted border
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


//banner display
@Composable
fun EcoBannerSlider(banners: List<BannerItem>, autoScrollDelayMillis: Long = 4000L, modifier: Modifier = Modifier) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(banners.size) {
        while (true) {
            delay(autoScrollDelayMillis)
            if (banners.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % banners.size //ready for next page
                pagerState.animateScrollToPage(nextPage) // the scrolling with animation
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
        HorizontalPager(state = pagerState) { page -> // just like forEach to for loop out of the banner inside the container
            val banner = banners[page]

            Image(
                painter = painterResource(id = banner.image),
                contentDescription = banner.title ?: "Eco banner image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(banners.size) { index ->
                val color = if (pagerState.currentPage == index) Color.White else Color.White.copy(alpha = 0.5f)
                Box(modifier = Modifier.size(6.dp).background(color, RoundedCornerShape(3.dp))) // the dot under the banner
            }
        }
    }
}

@Composable
fun EcoFeatureGrid(features: List<FeatureCardItem>, onFeatureClick: (String) -> Unit, modifier: Modifier = Modifier) {
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
fun EcoFeatureTile(feature: FeatureCardItem, onClick: () -> Unit, modifier: Modifier = Modifier) {

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
                .background(Color(0xFF1565C0)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = feature.image),
                contentDescription = feature.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1565C0).copy(alpha = 0.15f))
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = feature.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun resolveImageModel(imageUrl: String?, defaultPlaceholderRes: Int): Any {
    val context = LocalContext.current
    return remember(imageUrl) {
        when {
            imageUrl.isNullOrBlank() -> defaultPlaceholderRes
            imageUrl.startsWith("http://", ignoreCase = true) ||
                    imageUrl.startsWith("https://", ignoreCase = true) -> imageUrl
            else -> {
                val resId = context.resources.getIdentifier(
                    imageUrl.trim(), "drawable", context.packageName
                )
                if (resId != 0) resId else defaultPlaceholderRes
            }
        }
    }
}

@Composable
fun HotRewardsMarket(supabaseClient: SupabaseClient, currentUserId: String, onNavigateToRewards: () -> Unit, modifier: Modifier = Modifier) {
    var marketVouchers by remember { mutableStateOf<List<CampusVoucher>>(emptyList()) }
    var userHeldCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }// track users hpw many they are holding (max:3)
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedVoucher by remember { mutableStateOf<CampusVoucher?>(null) }

    LaunchedEffect(currentUserId) {// supabase to load data
        try {
            isLoading = true
            withContext(Dispatchers.IO) {
                val allVouchers = supabaseClient.from("campus_vouchers")
                    .select()
                    .decodeList<CampusVoucher>()

                marketVouchers = VoucherRules.pickFeaturedHomeVouchers(
                    vouchers = allVouchers,
                    title = { it.title },
                    isCatalogStock = { !it.isRedeemed && it.redeemedBy.isNullOrBlank() }
                )

                val userActiveVouchers = allVouchers.filter {
                    it.redeemedBy == currentUserId && !it.isRedeemed
                }
                userHeldCounts = userActiveVouchers.groupingBy { it.title }.eachCount()
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "Failed to load vouchers"
        } finally {
            isLoading = false
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Hot Campus Vouchers",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2E7D32))
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error loading data",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            marketVouchers.isEmpty() -> {
                Text(
                    text = "No vouchers available at the moment.",
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    marketVouchers.forEach { voucher ->
                        val heldCount = userHeldCounts[voucher.title] ?: 0
                        VoucherCard(
                            voucher = voucher,
                            heldCount = heldCount,
                            onClick = { selectedVoucher = voucher }
                        )
                    }
                }
            }
        }
    }

    //call the dialogue function for voucher
    selectedVoucher?.let { voucher ->
        val heldCount = userHeldCounts[voucher.title] ?: 0
        VoucherDetailDialog(
            voucher = voucher,
            heldCount = heldCount,
            onDismiss = { selectedVoucher = null },
            onGoToRewards = {
                selectedVoucher = null
                onNavigateToRewards()
            }
        )
    }
}

@Composable
fun VoucherCard(voucher: CampusVoucher, heldCount: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isLimitReached = VoucherRules.isAtHoldLimit(heldCount)
    val imageModel = resolveImageModel(voucher.imageUrl, R.drawable.img_placeholder_voucher)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLimitReached) Color(0xFFF5F5F5) else Color(0xFFF1F8E9)
        ),
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
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = voucher.title,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.img_placeholder_voucher),
                    error = painterResource(R.drawable.img_placeholder_voucher),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (voucher.merchantName.isNotBlank()) {
                    Text(
                        text = voucher.merchantName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C757D)
                    )
                }
                Text(
                    text = voucher.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${voucher.pointsCost} Coins",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = if (isLimitReached) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (isLimitReached) "Limit Reached ($heldCount/${VoucherRules.MAX_HELD_PER_TYPE})" else "Held: $heldCount/${VoucherRules.MAX_HELD_PER_TYPE}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLimitReached) Color(0xFFC62828) else Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
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
fun VoucherDetailDialog(voucher: CampusVoucher, heldCount: Int, onDismiss: () -> Unit, onGoToRewards: () -> Unit) {
    val isLimitReached = VoucherRules.isAtHoldLimit(heldCount)
    val imageModel = resolveImageModel(voucher.imageUrl, R.drawable.img_placeholder_submission)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = Color(0xFF212529),
        textContentColor = Color(0xFF495057),
        title = {
            Column {
                if (voucher.merchantName.isNotBlank()) {
                    Text(
                        text = voucher.merchantName,
                        fontSize = 12.sp,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = voucher.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F3F5))
                ) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = voucher.title,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.img_placeholder_submission),
                        error = painterResource(R.drawable.img_placeholder_submission),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${voucher.pointsCost} Coins",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = if (isLimitReached) Color(0xFFFFEBEE) else Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Holding: $heldCount/${VoucherRules.MAX_HELD_PER_TYPE}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLimitReached) Color(0xFFC62828) else Color(0xFF1565C0),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                if (isLimitReached) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Holding limit reached! You can hold up to ${VoucherRules.MAX_HELD_PER_TYPE} active copies of this voucher at once.",
                                fontSize = 11.sp,
                                color = Color(0xFFE65100),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onGoToRewards,
                enabled = !isLimitReached,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    disabledContainerColor = Color(0xFFBDBDBD),
                    disabledContentColor = if (isLimitReached) Color(0xFFC62828) else Color(0xFF9E9E9E)
                )
            ) {
                Text(if (isLimitReached) "Limit Reached" else "Go to Rewards")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Close", color = Color.Black) }
        }
    )
}
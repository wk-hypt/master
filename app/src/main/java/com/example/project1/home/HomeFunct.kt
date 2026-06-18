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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.data.LuckinBannerEntity
import com.example.project1.data.LuckinFeatureEntity
import com.example.project1.data.LuckinProductEntity

@Composable
fun HomeFunct(
    banners: List<LuckinBannerEntity>,
    features: List<LuckinFeatureEntity>,
    products: List<LuckinProductEntity>,
    modifier: Modifier = Modifier
) {
    val displayBanners = if (banners.isEmpty()) {
        listOf(LuckinBannerEntity(id = 1, imageUrl = "", title = "Welcome to Luckin", subtitle = "Freshly Brewed Everyday", targetRoute = "menu"),
            LuckinBannerEntity(id = 2, imageUrl = "", title = "Buy 1 Free 1", subtitle = "On Selected Drinks", targetRoute = "menu")
        )
    } else banners

    val displayFeatures = if (features.isEmpty()) {
        listOf(
            LuckinFeatureEntity(id = 1, title = "Now Ordering", subtitle = "Café Express", bgColorHex = "#1A4C8B"),
            LuckinFeatureEntity(id = 2, title = "Buy 1 Free 1", subtitle = "Voucher Club", bgColorHex = "#D32F2F")
        )
    } else features

    val displayProducts = if (products.isEmpty()) {
        listOf(
            LuckinProductEntity(id = 1, name = "Caffè Latte", price = 12.0, originalPrice = 15.0, discountTag = "Best Seller"),
            LuckinProductEntity(id = 2, name = "Coconut Latte", price = 13.5, originalPrice = 16.0, discountTag = "New"),
            LuckinProductEntity(id = 3, name = "Americano", price = 9.0, originalPrice = 12.0, discountTag = "Promo")
        )
    } else products

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LuckinBannerSlider(banners = displayBanners)
        Spacer(modifier = Modifier.height(16.dp))
        LuckinFeatureGrid(features = displayFeatures)
        Spacer(modifier = Modifier.height(16.dp))
        LuckinProductSection(products = displayProducts)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LuckinBannerSlider(
    banners: List<LuckinBannerEntity>,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { banners.size })

    Box(modifier = modifier.fillMaxWidth().height(200.dp)) {
        HorizontalPager(state = pagerState) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (page == 0) Color(0xFF1A4C8B) else Color(0xFF3A6CBB)),
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
fun LuckinFeatureGrid(
    features: List<LuckinFeatureEntity>,
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
fun LuckinProductSection(
    products: List<LuckinProductEntity>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Best Sellers", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "More", fontSize = 12.sp, color = Color.Gray)
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                LuckinProductCard(product = product)
            }
        }
    }
}

@Composable
fun LuckinProductCard(product: LuckinProductEntity) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray)) {
                Box(
                    modifier = Modifier.background(Color(0xFFFFEBEB), RoundedCornerShape(bottomEnd = 8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = product.discountTag, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "RM${String.format("%.2f", product.price)}", color = Color(0xFFD32F2F), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "RM${String.format("%.2f", product.originalPrice)}", color = Color.Gray, fontSize = 10.sp, textDecoration = TextDecoration.LineThrough)
                }
            }
        }
    }
}
package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

data class BottomNavBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(1000)
        installSplashScreen()

        setContent {
            MaterialTheme {
                val items = listOf(
                    BottomNavBarItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home
                    ),
                    BottomNavBarItem(
                        title = "eShop",
                        selectedIcon = Icons.Filled.ShoppingCart,
                        unselectedIcon = Icons.Outlined.ShoppingCart
                    ),
                    BottomNavBarItem(
                        title = "",
                        selectedIcon = Icons.Filled.QrCodeScanner,
                        unselectedIcon = Icons.Outlined.QrCodeScanner
                    ),
                    BottomNavBarItem(
                        title = "Gofinance",
                        selectedIcon = Icons.Filled.TrendingUp,
                        unselectedIcon = Icons.Outlined.TrendingUp
                    ),
                    BottomNavBarItem(
                        title = "Near me",
                        selectedIcon = Icons.Filled.Place,
                        unselectedIcon = Icons.Outlined.Place
                    )
                )
                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }

                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.surfaceBg)
                ) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar() {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = {
                                            selectedItemIndex = index

                                            val targetRoute = when (index) {
                                                0 -> "Home"
                                                1 -> "eShop"
                                                2 -> "Scan"
                                                3 -> "Gofinance"
                                                4 -> "Near me"
                                                else -> "Home"
                                            }

                                            navController.navigate(targetRoute) {
                                                popUpTo("Home") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        label = {
                                            Text(item.title)
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = if (index == selectedItemIndex) {
                                                    item.selectedIcon
                                                } else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "Home",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("Home") {
                                createHomePage()
                            }
                            composable("eShop") {
                                createEshopPage()
                            }
                            composable("Scan") {
                                createScanPage()
                            }
                            composable("Gofinance") {
                                createGOfinancePage()
                            }
                            composable("Near me") {
                                createNearMePage()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun createHomePage() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.surfaceBg))
        ) {
            item { TngHeaderSection() }
            item { TngQuickActionBar() }
            item { TngFeatureGridSection() }
            item { TngPromoBannerSection() }
            item {
                Text(
                    text = "Recommended Services",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                TngRecommendedServicesGrid()
            }
        }
    }

    @Composable
    fun createEshopPage() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.surfaceBg)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.budi95),
                    contentDescription = "Promo Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EshopTabButton(
                        "My Orders",
                        Icons.Default.LocalMall,
                        true,
                        modifier = Modifier.weight(1f)
                    )
                    EshopTabButton(
                        "Coming Soon",
                        Icons.Default.Campaign,
                        false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFBE6))
                        .padding(vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.budi95),
                                contentDescription = "eShop Logo",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "eShop",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF155CB4)
                            )
                        }
                        Text(
                            text = "View more",
                            fontSize = 13.sp,
                            color = Color(0xFF155CB4),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            EshopProductCard("TNG Card Riang Ria Raya", "RM25.00", R.drawable.gift)
                        }
                        item {
                            EshopProductCard(
                                "TNG Card Senyuman Lebaran",
                                "RM25.00",
                                R.drawable.gift
                            )
                        }
                        item {
                            EshopProductCard("TNG Card Special Edition", "RM25.00", R.drawable.gift)
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF155CB4), RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "F&B",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Food & Beverage", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "View more",
                        fontSize = 13.sp,
                        color = Color(0xFF155CB4),
                        modifier = Modifier.clickable { }
                    )
                }
            }
        }
    }

    @Composable
    fun createScanPage() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {
            item {
                Text(
                    "Scan QR / Barcode",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .border(
                            2.dp,
                            colorResource(id = R.color.tngBlueMain),
                            RoundedCornerShape(16.dp)
                        )
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("[ Camera View Finder Active ]", color = Color.LightGray)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
            items(5) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                ) {
                    ListItem(
                        headlineContent = {
                            Text(
                                "Recent Scanner Code Data Log #${index + 1}",
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.QrCode,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun createGOfinancePage() {
        var selectedTab by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.surfaceBg))
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF155CB4),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFFF1C40F),
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Overview", fontSize = 15.sp, fontWeight = FontWeight.Medium) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Cash Flow", fontSize = 15.sp, fontWeight = FontWeight.Medium) }
                )
            }

            if (selectedTab == 0) {
                OverviewTabContent()
            } else {
                CashFlowTabContent()
            }
        }
    }

    @Composable
    fun OverviewTabContent() {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF155CB4))
                        .padding(start = 16.dp, end = 16.dp, bottom = 20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "My Wallet Balance",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "RM ****",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF4F7FA), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.banner1),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Start your GOfinance journey!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Completed 0/3 steps", fontSize = 11.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Show steps", fontSize = 11.sp, color = Color(0xFF155CB4))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF155CB4),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable { }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFF1C40F),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "You have spent RM238.12 this month!",
                        fontSize = 12.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            item {
                OverviewPromoCarousel()
            }

            item {
                FinanceProductCard(title = "CashLoan") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(85.dp)
                            .background(Color(0xFFEDF3FA), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Loan Up To", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "RM150,000",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Get extra cash with flexible plans tailored to you!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(1.dp, Color(0xFF155CB4), RoundedCornerShape(20.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            "Apply Now",
                            color = Color(0xFF155CB4),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item {
                FinanceProductCard(title = "GO+") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFFFF9E6), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.banner1),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Earn up to 3.03% p.a. daily returns instantly with GO+.",
                            fontSize = 13.sp,
                            color = Color.Black,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .border(1.dp, Color(0xFF155CB4), RoundedCornerShape(20.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text(
                            "Start Earning",
                            color = Color(0xFF155CB4),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F8F5), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            "Over 3.7 million users are earning returns daily",
                            color = Color(0xFF2E4053),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            item {
                FinanceProductCard(title = "Investment") {
                    Column {
                        Text("Total Assets Value", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            "RM ****",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun FinanceProductCard(title: String, content: @Composable ColumnScope.() -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF155CB4)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFF155CB4),
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                content()
            }
        }
    }

    @Composable
    fun createNearMePage() {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.surfaceBg)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Near Me Merchants", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(12) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    ListItem(
                        headlineContent = { Text("Merchant Outlet Terminal #${index + 1}", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("${(index * 0.4) + 0.2} km away • Puchong, Selangor", color = Color.Gray) },
                        trailingContent = { Icon(Icons.Default.Navigation, contentDescription = null, tint = colorResource(id = R.color.tngBlueMain)) },
                        leadingContent = { Icon(Icons.Default.Place, contentDescription = null, tint = Color.Red) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

// home page
@Composable
fun TngHeaderSection(balanceAmount: Double = 12.66) {
    var isBalanceInvisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.tngBlueMain))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Box(
//                modifier = Modifier
//                    .background(Color(0xFFE56A53), shape = RoundedCornerShape(50))
//                    .padding(horizontal = 12.dp, vertical = 6.dp)
//            ) {
//                Text("China Trip", color = Color.White, fontSize = 12.sp)
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            OutlinedTextField(
//                value = "",
//                onValueChange = {},
//                placeholder = { Text("BUDI95", fontSize = 14.sp) },
//                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
//                colors = OutlinedTextFieldDefaults.colors(
//                    focusedContainerColor = Color.White,
//                    unfocusedContainerColor = Color.White,
//                    focusedBorderColor = Color.Transparent,
//                    unfocusedBorderColor = Color.Transparent
//                ),
//                shape = RoundedCornerShape(50),
//                modifier = Modifier
//                    .weight(1f)
//                    .height(48.dp),
//                readOnly = true
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Box(modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.2f), CircleShape))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (isBalanceInvisible) "RM ****" else String.format("RM %.2f", balanceAmount),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = if (isBalanceInvisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = if (isBalanceInvisible) "Show Balance" else "Hide Balance" ,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { isBalanceInvisible = !isBalanceInvisible }
            )
        }
        Text("View Asset Details >", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
            ) { Text("+ Reload", fontWeight = FontWeight.Bold) }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
            ) { Text("History >", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun TngQuickActionBar() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-12).dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickActionItem("Apply", R.drawable.apply)
            QuickActionItem("Cash Flow", R.drawable.cashflow)
            QuickActionItem("Transfer", R.drawable.transfer)
            QuickActionItem("Card Pocket", R.drawable.wallet)
        }
    }
}

@Composable
fun QuickActionItem(label: String, imageResId: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { }
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = label,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
fun TngFeatureGridSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallFeatureCard(
                title = "Grow Wealth",
                subtitle = "From RM10 onwards",
                stringColor = Color.Gray,
                imageResId = R.drawable.flower
            )
            SmallFeatureCard(
                title = "GOrewards",
                subtitle = "Join Now",
                stringColor = colorResource(id = R.color.tngBlueMain),
                imageResId = R.drawable.gift
            )
        }

        TallBudiCard(
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SmallFeatureCard(title: String, subtitle: String, stringColor: Color, imageResId: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.gridCardBg)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = stringColor)
            }
        }
    }
}

@Composable
fun TallBudiCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(162.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.gridCardBg)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.budi95),
                    contentDescription = "BUDI95 Logo",
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("BUDI95", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                    Text("RON95 Price at RM1.99", fontSize = 10.sp, color = Color.Gray)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = colorResource(id = R.color.white),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Fuel Quota Left", fontSize = 11.sp, color = Color.Gray)
                    Text("200 litres", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFFE8F0FA),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalGasStation,
                        contentDescription = "Fuel Station",
                        tint = colorResource(id = R.color.tngBlueMain),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TngPromoBannerSection() {
    val bannerImages = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.banner5
    )

    val pagerState = rememberPagerState(pageCount = { bannerImages.size })

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = bannerImages[page]),
                    contentDescription = "Promo Banner ${page + 1}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(bannerImages.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(width = if (isSelected) 12.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF155CB4) else Color(0xFFD9D9D9))
                )
            }
        }
    }
}

@Composable
fun TngRecommendedServicesGrid() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ServiceShortcutItem("Currency C...", "💱")
        ServiceShortcutItem("e-Mas", "👑")
        ServiceShortcutItem("WalletSafe", "🛡️")
    }
}

@Composable
fun ServiceShortcutItem(name: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(CircleShape).clickable { }.padding(8.dp)
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color.White, CircleShape).border(1.dp, Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 20.sp) }
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, fontSize = 11.sp)
    }
}


// Gofinance Page
@Composable
fun OverviewPromoCarousel() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDF3FA)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("RM20 CASHBACK 💰", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Get RM20 CASHBACK when you purchase Car Insurance + Road Tax with us! Use promo code: TELE8", fontSize = 11.sp, color = Color.DarkGray, lineHeight = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color(0xFF155CB4), RoundedCornerShape(14.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text("Free Quote", color = Color(0xFF155CB4), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(width = if (isSelected) 12.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF155CB4) else Color.LightGray)
                )
            }
        }
    }
}

@Composable
fun CashFlowTabContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("May 2026", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Income", color = Color.Gray, fontSize = 12.sp)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F0FA), RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0xFF155CB4), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Expenses", color = Color(0xFF155CB4), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .border(14.dp, Color(0xFFE59866), CircleShape)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Expenses", fontSize = 11.sp, color = Color.Gray)
                    Text("RM 238.12", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                CashFlowRowItem("Food & Beverage", "29% (10 transactions)", "RM69.70", Color(0xFFE59866))
                CashFlowRowItem("Transfers", "26% (6 transactions)", "RM61.16", Color(0xFFF4D03F))
                CashFlowRowItem("Services", "24% (2 transactions)", "RM56.06", Color(0xFF5499C7))
                CashFlowRowItem("Shopping", "22% (6 transactions)", "RM51.20", Color(0xFF48C9B0))
            }
        }
    }
}

@Composable
fun CashFlowRowItem(category: String, stats: String, amount: String, indicatorColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFF4F7FA), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(16.dp).background(indicatorColor, CircleShape))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(category, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Text(stats, fontSize = 11.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { 0.5f },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = indicatorColor,
                trackColor = Color(0xFFF4F7FA)
            )
        }
        Text(amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}


// Eshop page
@Composable
fun EshopTabButton(label: String, icon: ImageVector, isActive: Boolean, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .height(45.dp)
            .clickable { },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFFF0F4FA) else Color(0xFFF7F7F7)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color(0xFF155CB4) else Color.LightGray,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = if (isActive) Color(0xFF333333) else Color.LightGray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EshopProductCard(title: String, price: String, imageResId: Int) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable { }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Fit
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        color = Color(0xFFE67E22),
                        shape = RoundedCornerShape(bottomStart = 8.dp, topEnd = 8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("Limited", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = price,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF155CB4)
        )
    }
}

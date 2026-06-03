package com.example.kachingaapp_prog7313poe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kachingaapp_prog7313poe.navigation.NavRoutes
import com.example.kachingaapp_prog7313poe.ui.theme.BounceIn
import com.example.kachingaapp_prog7313poe.ui.theme.KachingaGreen
import com.example.kachingaapp_prog7313poe.ui.theme.KachingaGreenLight
import com.example.kachingaapp_prog7313poe.ui.theme.TextPrimary
import com.example.kachingaapp_prog7313poe.ui.theme.TextSecondary
import com.example.kachingaapp_prog7313poe.viewmodel.CategoryViewModel

@Composable
fun CategoriesScreen(
    onBackClick: () -> Unit,
    categoryViewModel: CategoryViewModel,
    onNavigate: (String) -> Unit
) {
    val categories by categoryViewModel.allCategories.collectAsState()

    AnimatedScreen {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0FAF4))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 90.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KachingaGreen)
                        .padding(horizontal = 20.dp)
                        .padding(top = 48.dp, bottom = 24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { onBackClick() }
                                    .padding(10.dp)
                            )
                            Text(
                                "Categories",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(
                                Pair("Total Balance", "R 7,783.00"),
                                Pair("Income", "R 7,783.00"),
                                Pair("Expense", "-R 1,187.40")
                            ).forEach { (label, value) ->
                                Column {
                                    Text(
                                        label,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        value,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Categories grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (categories.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No categories yet. Tap Add New!",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        categories.chunked(3).forEach { rowItems ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowItems.forEach { category ->
                                    BounceIn {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.width(90.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(64.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(KachingaGreenLight),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(category.icon, fontSize = 26.sp)
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                category.name,
                                                fontSize = 12.sp,
                                                color = TextPrimary,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                                // Fill empty slots
                                repeat(3 - rowItems.size) {
                                    Spacer(modifier = Modifier.width(90.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(KachingaGreen)
                            .clickable { onNavigate(NavRoutes.ADD_CATEGORY) },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Add New Category",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(2.dp, KachingaGreen, RoundedCornerShape(12.dp))
                                .clickable { onNavigate(NavRoutes.CATEGORY_REPORT) }
                                .padding(bottom = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.PieChart, contentDescription = null,
                                    tint = KachingaGreen, modifier = Modifier.size(18.dp))
                                Text("View Spending Report", fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold, color = KachingaGreen)
                            }
                        }
                    }
                }
            }

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = 0,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun AnimatedScreen(content: @Composable () -> Unit) {
    content()
}
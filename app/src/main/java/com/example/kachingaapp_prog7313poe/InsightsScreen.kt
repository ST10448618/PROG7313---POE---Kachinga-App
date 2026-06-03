package com.example.kachingaapp_prog7313poe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kachingaapp_prog7313poe.ui.theme.*
import com.example.kachingaapp_prog7313poe.viewmodel.InsightsViewModel
import com.example.kachingaapp_prog7313poe.navigation.NavRoutes
import com.example.prog7313_poe_kachinga.AnimatedScreen
import com.example.prog7313_poe_kachinga.ui.theme.KachingaGreen
import com.example.prog7313_poe_kachinga.ui.theme.KachingaGreenLight
import com.example.prog7313_poe_kachinga.ui.theme.KachingaRed

@Composable
fun InsightsScreen(
    onBackClick: () -> Unit,
    insightsViewModel: InsightsViewModel = viewModel(),
    onNavigate: ((String) -> Unit)? = null
) {
    val state by insightsViewModel.insightState.collectAsState()

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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            "Smart Insights",
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
                                Icons.Filled.Analytics,
                                contentDescription = "Analytics",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {

                    // Savings Goal Progress
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.onTrack) KachingaGreenLight
                            else Color(0xFFFFF0F0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Monthly Savings Goal",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.onTrack) KachingaGreen else KachingaRed
                                )
                                Text(
                                    "${state.savingsGoalProgress}%",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.onTrack) KachingaGreen else KachingaRed
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { state.savingsGoalProgress / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (state.onTrack) KachingaGreen else KachingaRed,
                                trackColor = Color(0xFFE0E0E0)
                            )
                        }
                    }
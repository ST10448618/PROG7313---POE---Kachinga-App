package com.example.kachingaapp_prog7313poe

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.kachingaapp_prog7313poe.data.entity.SavingsGoal
import com.example.kachingaapp_prog7313poe.navigation.NavRoutes
import com.example.kachingaapp_prog7313poe.ui.theme.BounceIn
import com.example.kachingaapp_prog7313poe.ui.theme.KachingaGreen
import com.example.kachingaapp_prog7313poe.ui.theme.KachingaGreenLight
import com.example.kachingaapp_prog7313poe.ui.theme.TextPrimary
import com.example.kachingaapp_prog7313poe.ui.theme.TextSecondary
import com.example.kachingaapp_prog7313poe.viewmodel.SavingsViewModel

@Composable
fun SavingsScreen(
    onBackClick: () -> Unit,
    onGoalClick: (SavingsGoal) -> Unit,
    savingsViewModel: SavingsViewModel,
    currentRoute: String = NavRoutes.SAVINGS,
    onNavigate: (String) -> Unit
) {
    val goals by savingsViewModel.allGoals.collectAsState()

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
                                "Goals",
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
                                    contentDescription = null,
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
                                Pair("Total Goals", "${goals.size}"),
                                Pair("Completed", "${goals.count {
                                    it.targetAmount > 0 &&
                                            it.savedAmount >= it.targetAmount }}"),
                                Pair("In Progress", "${goals.count {
                                    it.savedAmount < it.targetAmount }}")
                            ).forEach { (label, value) ->
                                Column {
                                    Text(
                                        label,
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        value,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    if (goals.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("💰", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No savings goals yet",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "Create your first goal below",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    } else {
                        goals.forEachIndexed { index, goal ->
                            BounceIn(delay = index * 80) {
                                SavingsGoalCard(
                                    icon = goal.icon,
                                    name = goal.name,
                                    dates = "Target: R ${"%.2f".format(goal.targetAmount)}",
                                    saved = "R ${"%.2f".format(goal.savedAmount)}",
                                    target = "of R ${"%.2f".format(goal.targetAmount)}",
                                    progress = if (goal.targetAmount > 0)
                                        (goal.savedAmount / goal.targetAmount)
                                            .toFloat().coerceIn(0f, 1f)
                                    else 0f,
                                    onClick = { onGoalClick(goal) }
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(KachingaGreen)
                            .clickable { onNavigate(NavRoutes.ADD_SAVINGS_GOAL) },
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
                                "Add Savings Goal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = 3,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun SavingsGoalCard(
    icon: String,
    name: String,
    dates: String,
    saved: String,
    target: String,
    progress: Float,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(KachingaGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(dates, fontSize = 12.sp, color = TextSecondary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        saved,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(target, fontSize = 12.sp, color = TextSecondary)
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = KachingaGreen,
                trackColor = Color(0xFFE0E0E0)
            )

            Text(
                "${(progress * 100).toInt()}% saved",
                fontSize = 11.sp,
                color = KachingaGreen,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
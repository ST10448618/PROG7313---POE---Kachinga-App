package com.example.prog7313_poe_kachinga

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.prog7313_poe_kachinga.ui.theme.BounceIn
import com.example.prog7313_poe_kachinga.ui.theme.KachingaGreen
import com.example.prog7313_poe_kachinga.ui.theme.KachingaGreenLight
import com.example.prog7313_poe_kachinga.ui.theme.TextPrimary
import com.example.prog7313_poe_kachinga.ui.theme.TextSecondary
import com.example.prog7313_poe_kachinga.viewmodel.AchievementsViewModel
import com.example.prog7313_poe_kachinga.navigation.NavRoutes  // ADD THIS


@Composable
fun AchievementsScreen(
    onBackClick: () -> Unit,
    achievementsViewModel: AchievementsViewModel,
    currentRoute: String = NavRoutes.ACHIEVEMENTS,  // ADD THIS
    onNavigate: ((String) -> Unit)? = null  // ADD THIS

) {
    val achievements by achievementsViewModel.allAchievements.collectAsState()
    val currentLevel by achievementsViewModel.currentLevel.collectAsState()
    val xpInCurrentLevel by achievementsViewModel.xpInCurrentLevel.collectAsState()
    val xpProgress by achievementsViewModel.xpProgress.collectAsState()

    LaunchedEffect(Unit) {
        achievementsViewModel.checkAndAwardAchievements()
    }

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
                        .padding(top = 48.dp, bottom = 28.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
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
                                "Achievements",
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

                        // Level card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(KachingaGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🏆", fontSize = 28.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Level $currentLevel – Smart Saver",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        "$xpInCurrentLevel / 1000 XP",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { xpProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = KachingaGreen,
                                        trackColor = Color(0xFFE0E0E0)
                                    )
                                    Text(
                                        "${1000 - xpInCurrentLevel} XP until next level",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("⭐", fontSize = 22.sp)
                            }
                        }
                    }
                }

                // Badges
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Text(
                        "Badges Earned",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (achievements.isEmpty()) {
                        Text(
                            "Complete actions to earn badges!",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        achievements.chunked(3).forEachIndexed { rowIndex, row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEachIndexed { colIndex, achievement ->
                                    BounceIn(delay = (rowIndex * 3 + colIndex) * 80) {
                                        AchievementBadgeCard(
                                            icon = achievement.icon,
                                            name = achievement.name,
                                            description = achievement.description,
                                            earned = achievement.isEarned
                                        )
                                    }
                                }
                                repeat(3 - row.size) {
                                    Spacer(modifier = Modifier.width(90.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = 4
            )
        }
    }
}

@Composable
fun AchievementBadgeCard(
    icon: String,
    name: String,
    description: String,
    earned: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier.size(68.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (earned) KachingaGreenLight else Color(0xFFF5F5F5)
                    )
                    .then(
                        if (earned) Modifier.border(
                            1.dp,
                            KachingaGreen.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    icon,
                    fontSize = 28.sp,
                    color = if (earned) Color.Unspecified
                    else Color.Gray.copy(alpha = 0.4f)
                )
            }

            if (earned) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(KachingaGreen)
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", fontSize = 10.sp, color = Color.White,
                        fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCCCCCC))
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔒", fontSize = 9.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (earned) TextPrimary else TextSecondary,
            textAlign = TextAlign.Center
        )
        Text(
            description,
            fontSize = 10.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}
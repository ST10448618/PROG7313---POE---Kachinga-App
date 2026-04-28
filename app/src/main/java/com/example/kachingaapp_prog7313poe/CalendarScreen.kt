package com.example.prog7313_poe_kachinga

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prog7313_poe_kachinga.data.entity.AppTransaction
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.TransactionViewModel
import java.util.*

@Composable
fun CalendarScreen(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val today = remember { Calendar.getInstance() }
    var displayedYear by remember { mutableIntStateOf(today.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableIntStateOf(today.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(today.get(Calendar.DAY_OF_MONTH)) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val allTransactions by transactionViewModel.allTransactions.collectAsState()

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    val weeks = remember(displayedYear, displayedMonth) {
        buildCalendarWeeks(displayedYear, displayedMonth)
    }

    // Transactions on selected day
    val selectedDayTransactions = remember(
        allTransactions, selectedDay, displayedMonth, displayedYear
    ) {
        allTransactions.filter { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            cal.get(Calendar.DAY_OF_MONTH) == selectedDay &&
                    cal.get(Calendar.MONTH) == displayedMonth &&
                    cal.get(Calendar.YEAR) == displayedYear
        }
    }

    // Days in month that have transactions — for dot indicators
    val daysWithTransactions = remember(allTransactions, displayedMonth, displayedYear) {
        allTransactions.filter { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            cal.get(Calendar.MONTH) == displayedMonth &&
                    cal.get(Calendar.YEAR) == displayedYear
        }.map { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.date }
            cal.get(Calendar.DAY_OF_MONTH)
        }.toSet()
    }
    // Chart data for selected day
    val chartData = remember(selectedDayTransactions) {
        buildChartData(selectedDayTransactions)
    }

    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

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
                            "Calendar",
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
                }

                // Calendar card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    // Month navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0FAF4))
                                .clickable {
                                    if (displayedMonth == 0) {
                                        displayedMonth = 11; displayedYear--
                                    } else displayedMonth--
                                    selectedDay = 1
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("‹", fontSize = 20.sp, color = KachingaGreen,
                                fontWeight = FontWeight.Bold)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF0FAF4))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(monthNames[displayedMonth], fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold, color = KachingaGreen)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF0FAF4))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(displayedYear.toString(), fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold, color = KachingaGreen)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF0FAF4))
                                .clickable {
                                    if (displayedMonth == 11) {
                                        displayedMonth = 0; displayedYear++
                                    } else displayedMonth++
                                    selectedDay = 1
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("›", fontSize = 20.sp, color = KachingaGreen,
                                fontWeight = FontWeight.Bold)
                        }
                    }
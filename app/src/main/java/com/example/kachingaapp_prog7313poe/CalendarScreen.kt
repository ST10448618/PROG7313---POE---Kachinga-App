package com.example.kachingaapp_prog7313poe

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
import com.example.kachingaapp_prog7313poe.data.entity.AppTransaction
import com.example.kachingaapp_prog7313poe.ui.theme.*
import com.example.kachingaapp_prog7313poe.viewmodel.TransactionViewModel
import java.util.*
import com.example.kachingaapp_prog7313poe.navigation.NavRoutes


@Composable
fun CalendarScreen(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel = viewModel(),
    onNavigate: ((String) -> Unit)? = null  // ADD THIS

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

                    // Day labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        dayLabels.forEach { day ->
                            Text(
                                day, modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center, fontSize = 12.sp,
                                color = KachingaGreen, fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Calendar grid with transaction indicators
                    weeks.forEach { week ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        ) {
                            week.forEach { day ->
                                val isSelected = day == selectedDay && day != 0
                                val isToday = day == today.get(Calendar.DAY_OF_MONTH) &&
                                        displayedMonth == today.get(Calendar.MONTH) &&
                                        displayedYear == today.get(Calendar.YEAR)
                                val hasTransaction = day != 0 && daysWithTransactions.contains(day)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> KachingaGreen
                                                    isToday -> KachingaGreenLight
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable {
                                                if (day != 0) selectedDay = day
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (day != 0) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = day.toString(),
                                                    fontSize = 13.sp,
                                                    color = when {
                                                        isSelected -> Color.White
                                                        isToday -> KachingaGreen
                                                        else -> TextPrimary
                                                    },
                                                    fontWeight = if (isSelected || isToday)
                                                        FontWeight.Bold else FontWeight.Normal
                                                )
                                                if (hasTransaction) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(
                                                                if (isSelected) Color.White
                                                                else KachingaGreen
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Selected day summary
                    val selectedDateLabel = "${monthNames[displayedMonth]} $selectedDay, $displayedYear"
                    Text(
                        selectedDateLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Spends / Categories toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFFF0FAF4))
                            .padding(4.dp)
                    ) {
                        listOf("Spends", "Categories").forEachIndexed { index, label ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        if (selectedTab == index) KachingaGreen
                                        else Color.Transparent
                                    )
                                    .clickable { selectedTab = index },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    label, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == index) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedDayTransactions.isEmpty()) {
                        // Empty state for selected day
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📅", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No transactions on this day",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                "Tap + to add one",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                    } else {
                        // Real pie chart from selected day transactions
                        if (chartData.isNotEmpty()) {
                            Text(
                                "Spending breakdown",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Canvas(
                                    modifier = Modifier
                                        .size(280.dp, 160.dp)
                                        .align(Alignment.Center)
                                ) {
                                    val strokeWidth = 52f
                                    val diameter =
                                        size.width.coerceAtMost(size.height * 2) - strokeWidth
                                    val topLeft = Offset(
                                        (size.width - diameter) / 2f,
                                        size.height - diameter / 2f - strokeWidth / 2f
                                    )
                                    val arcSize = Size(diameter, diameter)
                                    var startAngle = 180f

                                    chartData.forEach { segment ->
                                        drawArc(
                                            color = segment.color,
                                            startAngle = startAngle,
                                            sweepAngle = 180f * segment.fraction,
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = Stroke(width = strokeWidth)
                                        )
                                        startAngle += 180f * segment.fraction
                                    }
                                }

                                // Labels on arc
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    chartData.take(3).forEach { segment ->
                                        Text(
                                            "${(segment.fraction * 100).toInt()}%",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Dynamic legend
                            Column(modifier = Modifier.fillMaxWidth()) {
                                chartData.forEach { segment ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(segment.color)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            segment.label,
                                            fontSize = 13.sp,
                                            color = TextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            "${"%.2f".format(segment.amount)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Transaction list for selected day
                        Text(
                            "Transactions",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        selectedDayTransactions.forEach { tx ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (tx.isExpense) KachingaRedLight
                                            else KachingaGreenLight
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(tx.categoryIcon, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        tx.title, fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold, color = TextPrimary
                                    )
                                    Text(
                                        tx.categoryName, fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    "${if (tx.isExpense) "-" else "+"}${"%.2f".format(tx.amount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.isExpense) KachingaRed else KachingaGreen
                                )
                            }
                            HorizontalDivider(color = Divider, thickness = 0.5.dp)
                        }
                    }
                }
            }

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = 1,
                onNavigate = { onBackClick() }
            )
        }
    }
}

// Data class for chart segments
data class ChartSegment(
    val label: String,
    val amount: Double,
    val fraction: Float,
    val color: Color
)

// Chart colour palette
private val chartColors = listOf(
    Color(0xFF1DB954),
    Color(0xFF0F6E56),
    Color(0xFF5DCAA5),
    Color(0xFFFF9800),
    Color(0xFFE91E63),
    Color(0xFF3F51B5),
    Color(0xFF9C27B0),
    Color(0xFFFF5722)
)

fun buildChartData(transactions: List<AppTransaction>): List<ChartSegment> {
    val expenses = transactions.filter { it.isExpense }
    if (expenses.isEmpty()) return emptyList()

    val total = expenses.sumOf { it.amount }
    if (total <= 0) return emptyList()

    // Group by category
    val grouped = expenses
        .groupBy { it.categoryName }
        .map { (name, txs) -> name to txs.sumOf { it.amount } }
        .sortedByDescending { it.second }

    return grouped.mapIndexed { index, (name, amount) ->
        ChartSegment(
            label = name,
            amount = amount,
            fraction = (amount / total).toFloat(),
            color = chartColors[index % chartColors.size]
        )
    }
}

fun buildCalendarWeeks(year: Int, month: Int): List<List<Int>> {
    val cal = Calendar.getInstance()
    cal.set(year, month, 1)

    val firstDayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val days = mutableListOf<Int>()
    repeat(firstDayOfWeek) { days.add(0) }
    for (d in 1..daysInMonth) days.add(d)
    while (days.size % 7 != 0) days.add(0)
    return days.chunked(7)
}

@Composable
fun LegendDot(color: Color) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}
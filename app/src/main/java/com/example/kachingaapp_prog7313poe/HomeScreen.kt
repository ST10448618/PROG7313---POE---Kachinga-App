package com.example.prog7313_poe_kachinga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prog7313_poe_kachinga.data.entity.AppTransaction
import com.example.prog7313_poe_kachinga.navigation.NavRoutes
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.HomeViewModel
import com.example.prog7313_poe_kachinga.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.prog7313_poe_kachinga.viewmodel.TimeFilter

fun formatAmount(amount: Double): String {
    return "%.2f".format(amount).replace(',', '.')
}

@Composable
fun HomeScreen(
    transactionViewModel: TransactionViewModel,
    onNavigate: (String) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val state by homeViewModel.financialState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Monthly") }

    val grouped = state.transactions.groupBy { monthLabel(it.date) }

    val savingsAmount = state.monthlySalary * (state.savingsTargetPct / 100.0)
    val remainingBudget = state.monthlySalary - state.totalExpenses

    // Extract just the symbol cleanly e.g. "ZAR (R)" -> "R"
    fun currencySymbol(): String {
        val c = state.currency
        return when {
            c.contains("R)") || c.contains("ZAR") -> "R"
            c.contains("$") -> "$"
            c.contains("€") -> "€"
            c.contains("£") -> "£"
            else -> "R"
        }
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
                // Green Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KachingaGreen)
                        .padding(horizontal = 20.dp)
                        .padding(top = 48.dp, bottom = 28.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Hi, Welcome Back!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "Good ${timeOfDay()}",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Balance Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Total Balance",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                "${currencySymbol()} ${formatAmount(state.balance)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Income
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(KachingaGreenLight)
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.ArrowDownward,
                                            contentDescription = null,
                                            tint = KachingaGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Income", fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Text(
                                        "${currencySymbol()} ${formatAmount(state.totalIncome)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KachingaGreen
                                    )
                                }

                                // Expense
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(KachingaRedLight)
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.ArrowUpward,
                                            contentDescription = null,
                                            tint = KachingaRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text("Expense", fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Text(
                                        "-${currencySymbol()} ${formatAmount(state.totalExpenses)}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KachingaRed
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (state.monthlySalary > 0) {
                                val pct = state.healthPercent.coerceIn(0, 100)
                                val barColor = when {
                                    pct < 50 -> KachingaGreen
                                    pct < 80 -> Color(0xFFFFA500)
                                    else -> KachingaRed
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        when {
                                            pct < 50 -> "✓ ${pct}% of salary spent"
                                            pct < 80 -> "⚠ ${pct}% of salary spent"
                                            else -> "✗ ${pct}% of salary spent"
                                        },
                                        fontSize = 12.sp,
                                        color = barColor
                                    )
                                    Text(
                                        "Budget left: ${currencySymbol()} ${formatAmount(remainingBudget.coerceAtLeast(0.0))}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { pct / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = barColor,
                                    trackColor = Color(0xFFE0E0E0)
                                )
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(KachingaGreenLight)
                                        .clickable { onNavigate(NavRoutes.PROFILE) }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = KachingaGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Set your salary in Profile to track budget",
                                        fontSize = 12.sp,
                                        color = KachingaGreen
                                    )
                                }
                            }

                            if (state.monthlySalary > 0 && state.savingsTargetPct > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "💰 Save target: ${currencySymbol()} ${formatAmount(savingsAmount)} (${state.savingsTargetPct.toInt()}% of salary)",
                                    fontSize = 12.sp,
                                    color = KachingaGreen
                                )
                            }
                        }
                    }
                }

                // Quick Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(Icons.Filled.Add, "Add", Modifier.weight(1f)) { onNavigate(NavRoutes.ADD_TRANSACTION) }
                    QuickActionButton(Icons.Filled.BarChart, "Stats", Modifier.weight(1f)) { onNavigate(NavRoutes.CALENDAR) }
                    QuickActionButton(Icons.Filled.Savings, "Goals", Modifier.weight(1f)) { onNavigate(NavRoutes.SAVINGS) }
                    QuickActionButton(Icons.Filled.EmojiEvents, "Awards", Modifier.weight(1f)) { onNavigate(NavRoutes.ACHIEVEMENTS) }
                }

                // Transactions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Transactions", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "See all", fontSize = 13.sp, color = KachingaGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigate(NavRoutes.TRANSACTIONS) }
                        )
                    }

                    val filterOptions = listOf("Daily", "Weekly", "Monthly")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        filterOptions.forEach { filter ->

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        if (selectedFilter == filter) KachingaGreen else Color.White
                                    )
                                    .clickable {
                                        selectedFilter = filter

                                        homeViewModel.setFilter(
                                            when (filter) {
                                                "Daily" -> TimeFilter.DAILY
                                                "Weekly" -> TimeFilter.WEEKLY
                                                else -> TimeFilter.MONTHLY
                                            }
                                        )
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    filter,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedFilter == filter) Color.White else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.transactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = KachingaGreen.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No transactions yet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("Tap + to add your first transaction", fontSize = 13.sp, color = TextSecondary)
                        }
                    } else {
                        grouped.forEach { (month, txList) ->
                            Text(
                                month, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
                            )
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    txList.forEachIndexed { index, tx ->
                                        HomeTransactionRow(
                                            transaction = tx,
                                            currencySymbol = currencySymbol(),
                                            onClick = { onNavigate(NavRoutes.TRANSACTIONS) }
                                        )
                                        if (index < txList.lastIndex) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                color = Divider,
                                                thickness = 0.5.dp
                                            )
                                        }
                                    }
                                }
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
fun HomeTransactionRow(
    transaction: AppTransaction,
    currencySymbol: String = "R",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (transaction.isExpense) KachingaRedLight else KachingaGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Text(transaction.categoryIcon, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("${transaction.categoryName}  •  ${formatDate(transaction.date)}", fontSize = 12.sp, color = TextSecondary)
        }
        Text(
            "${if (transaction.isExpense) "-" else "+"}$currencySymbol ${formatAmount(transaction.amount)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.isExpense) KachingaRed else KachingaGreen
        )
    }
}

@Composable
fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(KachingaGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = KachingaGreen, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 11.sp, color = TextSecondary)
    }
}

fun formatDate(timestamp: Long): String =
    SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))

fun monthLabel(timestamp: Long): String =
    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(timestamp))

fun timeOfDay(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 0..11 -> "Morning"
    in 12..17 -> "Afternoon"
    else -> "Evening"
}
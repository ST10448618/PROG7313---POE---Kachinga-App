package com.example.kachingaapp_prog7313poe

import android.app.DatePickerDialog
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kachingaapp_prog7313poe.data.entity.AppTransaction
import com.example.kachingaapp_prog7313poe.ui.theme.*
import com.example.kachingaapp_prog7313poe.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

data class CategoryTotal(
    val categoryName: String,
    val categoryIcon: String,
    val total: Double,
    val count: Int,
    val percentage: Float
)

@Composable
fun CategoryReportScreen(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel
) {
    val context = LocalContext.current
    val allTransactions by transactionViewModel.allTransactions.collectAsState()

    // Default period: current month
    val now = remember { Calendar.getInstance() }
    var startMillis by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        )
    }
    var endMillis by remember {
        mutableLongStateOf(
            Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
            }.timeInMillis
        )
    }

    var selectedPreset by remember { mutableStateOf("This Month") }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    // Filter transactions by selected period
    val filteredTransactions = remember(allTransactions, startMillis, endMillis) {
        allTransactions.filter { tx ->
            tx.isExpense && tx.date in startMillis..endMillis
        }
    }

    // Group by category and calculate totals
    val totalSpent = filteredTransactions.sumOf { it.amount }
    val categoryTotals = remember(filteredTransactions) {
        filteredTransactions
            .groupBy { it.categoryName }
            .map { (name, txs) ->
                val total = txs.sumOf { it.amount }
                CategoryTotal(
                    categoryName = name,
                    categoryIcon = txs.first().categoryIcon,
                    total = total,
                    count = txs.size,
                    percentage = if (totalSpent > 0) (total / totalSpent * 100).toFloat() else 0f
                )
            }
            .sortedByDescending { it.total }
    }

    fun showDatePicker(isStart: Boolean) {
        val cal = Calendar.getInstance().apply {
            timeInMillis = if (isStart) startMillis else endMillis
        }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val newCal = Calendar.getInstance()
                newCal.set(year, month, day)
                if (isStart) {
                    newCal.set(Calendar.HOUR_OF_DAY, 0)
                    newCal.set(Calendar.MINUTE, 0)
                    newCal.set(Calendar.SECOND, 0)
                    startMillis = newCal.timeInMillis
                } else {
                    newCal.set(Calendar.HOUR_OF_DAY, 23)
                    newCal.set(Calendar.MINUTE, 59)
                    newCal.set(Calendar.SECOND, 59)
                    endMillis = newCal.timeInMillis
                }
                selectedPreset = "Custom"
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun applyPreset(preset: String) {
        selectedPreset = preset
        val cal = Calendar.getInstance()
        when (preset) {
            "This Week" -> {
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                startMillis = cal.timeInMillis
                endMillis = System.currentTimeMillis()
            }
            "This Month" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                startMillis = cal.timeInMillis
                endMillis = System.currentTimeMillis()
            }
            "Last Month" -> {
                cal.add(Calendar.MONTH, -1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                startMillis = cal.timeInMillis
                val endCal = Calendar.getInstance().apply {
                    add(Calendar.MONTH, -1)
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                }
                endMillis = endCal.timeInMillis
            }
            "This Year" -> {
                cal.set(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
                startMillis = cal.timeInMillis
                endMillis = System.currentTimeMillis()
            }
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
                                "Category Report",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.size(44.dp))
                        }

                        // Total summary
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Total Spent",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    "R ${"%.2f".format(totalSpent)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = KachingaRed
                                )
                                Text(
                                    "${filteredTransactions.size} transactions  •  ${categoryTotals.size} categories",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {

                    // Period preset chips
                    Text(
                        "Select Period",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("This Week", "This Month", "Last Month", "This Year")
                            .forEach { preset ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(
                                            if (selectedPreset == preset) KachingaGreen
                                            else Color.White
                                        )
                                        .clickable { applyPreset(preset) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        preset,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedPreset == preset) Color.White
                                        else TextSecondary
                                    )
                                }
                            }
                    }

                    // Custom date range
                    Text(
                        "Or pick custom dates",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable { showDatePicker(true) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("From", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    dateFormat.format(Date(startMillis)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .clickable { showDatePicker(false) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("To", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    dateFormat.format(Date(endMillis)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // Category breakdown
                    Text(
                        "Spending by Category",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (categoryTotals.isEmpty()) {
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
                                Text("📊", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No expenses in this period",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "Try a different date range",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                categoryTotals.forEachIndexed { index, cat ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(KachingaRedLight),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(cat.categoryIcon, fontSize = 20.sp)
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        cat.categoryName,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                    Text(
                                                        "R ${"%.2f".format(cat.total)}",
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = KachingaRed
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        "${cat.count} transaction${if (cat.count != 1) "s" else ""}",
                                                        fontSize = 12.sp,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        "${"%.1f".format(cat.percentage)}%",
                                                        fontSize = 12.sp,
                                                        color = TextSecondary
                                                    )
                                                }

                                                Spacer(modifier = Modifier.height(6.dp))

                                                // Progress bar
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(6.dp)
                                                        .clip(RoundedCornerShape(3.dp))
                                                        .background(Color(0xFFE0E0E0))
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth(cat.percentage / 100f)
                                                            .height(6.dp)
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(KachingaRed)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (index < categoryTotals.lastIndex) {
                                        HorizontalDivider(
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

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = 1,
                onNavigate = { onBackClick() }
            )
        }
    }
}
package com.example.prog7313_poe_kachinga

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
import com.example.prog7313_poe_kachinga.data.entity.AppTransaction
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.TransactionViewModel
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

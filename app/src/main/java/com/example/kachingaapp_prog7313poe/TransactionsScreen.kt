package com.example.prog7313_poe_kachinga

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.prog7313_poe_kachinga.data.entity.AppTransaction
import com.example.prog7313_poe_kachinga.navigation.NavRoutes
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.TransactionViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel,
    onNavigate: ((String) -> Unit)? = null
){
    val transactions by transactionViewModel.allTransactions.collectAsState()
    val balance by transactionViewModel.balance.collectAsState()
    val totalIncome by transactionViewModel.totalIncome.collectAsState()
    val totalExpenses by transactionViewModel.totalExpenses.collectAsState()
    val uiState by transactionViewModel.uiState.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var showDeleteDialog by remember { mutableStateOf<AppTransaction?>(null) }
    var viewingImage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    val filteredTransactions = when (selectedFilter) {
        "Income" -> transactions.filter { !it.isExpense }
        "Expense" -> transactions.filter { it.isExpense }
        else -> transactions
    }
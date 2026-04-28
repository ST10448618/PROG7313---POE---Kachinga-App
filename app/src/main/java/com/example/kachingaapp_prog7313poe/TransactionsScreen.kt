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
    val grouped = filteredTransactions.groupBy { monthLabel(it.date) }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            transactionViewModel.clearMessages()
        }
    }
// Full screen image viewer
    viewingImage?.let { path ->
        Dialog(onDismissRequest = { viewingImage = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .clickable { viewingImage = null }
            ) {
                AsyncImage(
                    model = File(path),
                    contentDescription = "Receipt",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                Text(
                    "Tap to close",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                )
            }
        }
    }
// Delete confirm dialog
    showDeleteDialog?.let { tx ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Transaction") },
            text = { Text("Remove \"${tx.title}\" permanently?") },
            confirmButton = {
                TextButton(onClick = {
                    transactionViewModel.deleteTransaction(tx)
                    showDeleteDialog = null
                }) {
                    Text("Delete", color = KachingaRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF0FAF4),
        bottomBar = {
            BottomNavBar(
                selectedIndex = 0,
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        AnimatedScreen {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ){
                // Green header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KachingaGreen)
                        .padding(horizontal = 20.dp)
                        .padding(top = 48.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back button
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { onBackClick() }
                                .padding(10.dp)
                        )
                        // Title
                        Text(
                            "Transactions",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        // Add transaction button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { onNavigate?.invoke(NavRoutes.ADD_TRANSACTION) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Transaction",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                      // Category report button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { onNavigate?.invoke(NavRoutes.CATEGORY_REPORT) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.PieChart,
                                contentDescription = "Category Report",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

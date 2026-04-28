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
                    // Summary card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Total Balance",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                            Text(
                                "R ${formatAmount(balance)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (balance < 0) KachingaRed else TextPrimary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(KachingaGreenLight)
                                        .padding(10.dp)
                                ) {
                                    Text("Income", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        "R ${formatAmount(totalIncome)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KachingaGreen
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(KachingaRedLight)
                                        .padding(10.dp)
                                ) {
                                    Text("Expenses", fontSize = 11.sp, color = TextSecondary)
                                    Text(
                                        "-R ${formatAmount(totalExpenses)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = KachingaRed
                                    )
                                }
                            }
                        }
                    }
                }
                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Income", "Expense").forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(
                                    when {
                                        isSelected && filter == "Expense" -> KachingaRed
                                        isSelected -> KachingaGreen
                                        else -> Color.White
                                    }
                                )
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                filter,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
              // Transaction list
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp)
                ) {
                    if (filteredTransactions.isEmpty()) {
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
                                Icon(
                                    Icons.AutoMirrored.Filled.ReceiptLong,
                                    contentDescription = null,
                                    tint = KachingaGreen.copy(alpha = 0.4f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "No transactions yet",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    "Tap + to add your first one",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        grouped.forEach { (month, txList) ->
                            Text(
                                month,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp, top = 4.dp)
                            )
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column {
                                    txList.forEachIndexed { index, tx ->
                                        FixedSwipeToDelete(
                                            onDelete = { showDeleteDialog = tx }
                                        ) {
                                            FullTransactionRow(
                                                transaction = tx,
                                                onImageClick = {
                                                    if (tx.imagePath.isNotBlank()) {
                                                        viewingImage = tx.imagePath
                                                    }
                                                }
                                            )
                                        }
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixedSwipeToDelete(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
            false // always false — keeps row, dialog confirms delete
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.4f }
    )

    val isSwipingLeft = dismissState.targetValue == SwipeToDismissBoxValue.EndToStart
    val bgColor by animateColorAsState(
        targetValue = if (isSwipingLeft) KachingaRed else Color.Transparent,
        label = "swipe_bg"
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            // Fixed: background only shows on the right side, doesn't fill the whole row content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .background(bgColor)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (isSwipingLeft) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Delete",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        content = { content() }
    )
}

@Composable
fun FullTransactionRow(
    transaction: AppTransaction,
    onImageClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (transaction.isExpense) KachingaRedLight else KachingaGreenLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(transaction.categoryIcon, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title + category + note
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "${transaction.categoryName}  •  ${formatDate(transaction.date)}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                if (transaction.note.isNotBlank()) {
                    Text(
                        transaction.note,
                        fontSize = 11.sp,
                        color = TextHint,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                if (transaction.startTime.isNotBlank() || transaction.endTime.isNotBlank()) {
                    Text(
                        buildString {
                            if (transaction.startTime.isNotBlank()) append(transaction.startTime)
                            if (transaction.startTime.isNotBlank() && transaction.endTime.isNotBlank()) append(" – ")
                            if (transaction.endTime.isNotBlank()) append(transaction.endTime)
                        },
                        fontSize = 11.sp,
                        color = TextHint,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount + receipt badge
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "${if (transaction.isExpense) "-" else "+"}R ${formatAmount(transaction.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.isExpense) KachingaRed else KachingaGreen
                )
                if (transaction.imagePath.isNotBlank() && File(transaction.imagePath).exists()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(KachingaGreenLight)
                            .clickable { onImageClick() }
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = "View receipt",
                            tint = KachingaGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            "Receipt",
                            fontSize = 10.sp,
                            color = KachingaGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
package com.example.prog7313_poe_kachinga

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prog7313_poe_kachinga.data.entity.Bill
import com.example.prog7313_poe_kachinga.navigation.NavRoutes
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.BillViewModel


@Composable
fun BillsScreen(
    onBackClick: () -> Unit,
    billViewModel: BillViewModel = viewModel(),
    onNavigate: ((String) -> Unit)? = null
) {
    val bills by billViewModel.allBills.collectAsState()
    val upcomingBills by billViewModel.upcomingBills.collectAsState()
    val totalMonthly by billViewModel.totalMonthlyBills.collectAsState()
    val uiState by billViewModel.uiState.collectAsState()

    var showDeleteDialog by remember {
        mutableStateOf<Bill?>(null)
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            billViewModel.clearMessages()
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { bill ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Bill") },
            text = { Text("Remove \"${bill.name}\" recurring payment?") },
            confirmButton = {
                TextButton(onClick = {
                    billViewModel.deleteBill(bill)
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
        containerColor = Color(0xFFF0FAF4)
    ) { paddingValues ->
        AnimatedScreen {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                                    "Bills & Reminders",
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
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .clickable {
                                            onNavigate?.invoke(NavRoutes.ADD_BILL)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Add Bill",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Monthly bills summary
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Total Monthly Bills",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        "R ${"%.2f".format(totalMonthly)}",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                    Text(
                                        "${bills.size} active bill${if (bills.size != 1) "s" else ""}",
                                        fontSize = 11.sp,
                                        color = TextHint,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }

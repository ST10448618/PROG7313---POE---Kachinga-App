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

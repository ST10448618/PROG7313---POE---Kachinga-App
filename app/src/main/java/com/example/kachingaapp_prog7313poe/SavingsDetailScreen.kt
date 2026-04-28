package com.example.prog7313_poe_kachinga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prog7313_poe_kachinga.navigation.NavRoutes
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.SavingsViewModel

@Composable
fun SavingsDetailScreen(
    goalId: Int,
    onBackClick: () -> Unit,
    savingsViewModel: SavingsViewModel,
    onNavigate: ((String) -> Unit)? = null
) {
    val goals by savingsViewModel.allGoals.collectAsState()
    val goal = goals.find { it.id == goalId }

    val uiState by savingsViewModel.uiState.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var depositAmount by remember { mutableStateOf("") }
    var depositError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            savingsViewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            savingsViewModel.clearMessages()
        }
    }

    // Deposit dialog
    if (showDepositDialog) {
        AlertDialog(
            onDismissRequest = {
                showDepositDialog = false
                depositAmount = ""
                depositError = null
            },
            title = {
                Text(
                    "Add Deposit",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        "How much would you like to deposit towards ${goal?.name}?",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = {
                            depositAmount = it
                            depositError = null
                        },
                        label = { Text("Amount (R)") },
                        isError = depositError != null,
                        supportingText = {
                            if (depositError != null) {
                                Text(depositError!!, color = KachingaRed)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KachingaGreen,
                            unfocusedBorderColor = Divider
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = depositAmount.toDoubleOrNull()
                        when {
                            amount == null || amount <= 0 -> {
                                depositError = "Enter a valid amount"
                            }
                            goal != null &&
                                    amount > (goal.targetAmount - goal.savedAmount) -> {
                                depositError = "Amount exceeds remaining target"
                            }
                            else -> {
                                savingsViewModel.deposit(goalId, amount!!)
                                showDepositDialog = false
                                depositAmount = ""
                                depositError = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KachingaGreen
                    )
                ) {
                    Text("Deposit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDepositDialog = false
                        depositAmount = ""
                        depositError = null
                    }
                ) {
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
                if (goal == null) {
                    // Goal not found state
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.SearchOff,
                            contentDescription = null,
                            tint = KachingaGreen.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Goal not found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onBackClick) {
                            Text("Go Back", color = KachingaGreen)
                        }
                    }
                } else {
                    val progress = if (goal.targetAmount > 0)
                        (goal.savedAmount / goal.targetAmount)
                            .toFloat().coerceIn(0f, 1f)
                    else 0f

                    val remaining = goal.targetAmount - goal.savedAmount
                    val isComplete = progress >= 1f

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 90.dp)
                    ) {
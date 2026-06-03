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

                        // ── Green Header ──────────────────────────────
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(KachingaGreen)
                                .padding(horizontal = 20.dp)
                                .padding(top = 48.dp, bottom = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Top bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clickable { onBackClick() }
                                        .wrapContentSize(Alignment.Center)
                                )
                                Text(
                                    goal.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                                // Delete goal
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .clickable {
                                            savingsViewModel.deleteGoal(goal)
                                            onBackClick()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete goal",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Goal icon
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        if (isComplete) Color(0xFFFFD700)
                                        else KachingaGreenDark
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (isComplete) "🏆" else goal.icon,
                                    fontSize = 40.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (isComplete) {
                                Text(
                                    "Goal Complete! 🎉",
                                    fontSize = 14.sp,
                                    color = Color(0xFFFFD700),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            // Saved amount
                            Text(
                                "R ${"%.2f".format(goal.savedAmount)}",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "saved of R ${"%.2f".format(goal.targetAmount)}",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Progress bar
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        "${(progress * 100).toInt()}% complete",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        if (isComplete) "Completed!"
                                        else "R ${"%.2f".format(remaining)} left",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp)),
                                    color = Color.White,
                                    trackColor = Color.White.copy(alpha = 0.3f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ── Stats Row ─────────────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label = "Target",
                                value = "R ${"%.0f".format(goal.targetAmount)}",
                                icon = Icons.Filled.Flag,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "Saved",
                                value = "R ${"%.0f".format(goal.savedAmount)}",
                                icon = Icons.Filled.Savings,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "Remaining",
                                value = "R ${"%.0f".format(remaining.coerceAtLeast(0.0))}",
                                icon = Icons.Filled.AccessTime,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // ── Add Deposit Button ────────────────────────
                        if (!isComplete) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(KachingaGreen)
                                    .clickable { showDepositDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        "Add Deposit",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // ── Tip Card ──────────────────────────────────
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = KachingaGreenLight
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Lightbulb,
                                    contentDescription = null,
                                    tint = KachingaGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                val daysLeft = if (progress < 1f && goal.savedAmount > 0) {
                                    val ratePerDay = goal.savedAmount /
                                            ((System.currentTimeMillis() - 0L) /
                                                    (1000 * 60 * 60 * 24.0)).coerceAtLeast(1.0)
                                    if (ratePerDay > 0)
                                        (remaining / ratePerDay).toInt()
                                    else null
                                } else null

                                Text(
                                    text = when {
                                        isComplete ->
                                            "Amazing! You reached your goal. Set a new one!"
                                        daysLeft != null ->
                                            "At your current pace, you'll reach this goal in ~$daysLeft days."
                                        else ->
                                            "Keep saving consistently to reach your goal faster!"
                                    },
                                    fontSize = 12.sp,
                                    color = KachingaGreenDark
                                )
                            }
                        }
                    }
                }

                BottomNavBar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    selectedIndex = 3,
                    onNavigate = onNavigate
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = KachingaGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                label,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}
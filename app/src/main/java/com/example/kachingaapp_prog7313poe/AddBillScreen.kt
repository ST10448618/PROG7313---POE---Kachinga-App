package com.example.kachingaapp_prog7313poe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kachingaapp_prog7313poe.ui.theme.*
import com.example.kachingaapp_prog7313poe.viewmodel.BillViewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore

@Composable
fun AddBillScreen(
    onBackClick: () -> Unit,
    billViewModel: BillViewModel = viewModel(),
    onSuccess: () -> Unit
) {
    var billName by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var dueDay by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Subscription") }
    var selectedIcon by remember { mutableStateOf("🧾") }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }

    val uiState by billViewModel.uiState.collectAsState()

    val billCategories = listOf(
        "Subscription" to "📱",
        "Utilities" to "⚡",
        "Rent" to "🏠",
        "Insurance" to "🛡",
        "Phone" to "☎",
        "Internet" to "📡",
        "Gym" to "💪",
        "Entertainment" to "🎬"
    )

    val billIcons = listOf("🧾", "📱", "⚡", "🏠", "🛡", "☎", "📡", "💪", "🎬", "🚗", "💳", "📺")

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            billViewModel.clearMessages()
            onSuccess()
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
                    .padding(bottom = 40.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KachingaGreen)
                        .padding(horizontal = 20.dp)
                        .padding(top = 48.dp, bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                            "Add Bill Reminder",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(44.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    // Bill name
                    FormLabel("Bill Name")
                    OutlinedTextField(
                        value = billName,
                        onValueChange = { billName = it },
                        placeholder = { Text("e.g. Netflix, Rent, Electricity", color = TextHint) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = kachingaTextFieldColors()
                    )

                    // Amount
                    FormLabel("Amount (R)")
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = { Text("0.00", color = TextHint) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = kachingaTextFieldColors()
                    )

                    // Due day
                    FormLabel("Due Day (1-31)")
                    OutlinedTextField(
                        value = dueDay,
                        onValueChange = { dueDay = it.take(2) },
                        placeholder = { Text("e.g. 15", color = TextHint) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = kachingaTextFieldColors()
                    )

                    // Category
                    FormLabel("Category")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF7F7F7))
                            .clickable { showCategoryPicker = !showCategoryPicker }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                selectedCategory,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (showCategoryPicker) Icons.Filled.ExpandLess
                                else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    }

                    if (showCategoryPicker) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF7F7F7))
                                .padding(8.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            billCategories.forEach { (cat, icon) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (selectedCategory == cat)
                                                KachingaGreenLight else Color.Transparent
                                        )
                                        .clickable {
                                            selectedCategory = cat
                                            selectedIcon = icon
                                            showCategoryPicker = false
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(icon, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(cat, fontSize = 14.sp, color = TextPrimary)
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Icon picker
                    FormLabel("Bill Icon")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF7F7F7))
                            .clickable { showIconPicker = !showIconPicker }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(selectedIcon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Tap to change",
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (showIconPicker) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text(
                                "Choose icon:",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF7F7F7))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                billIcons.forEach { icon ->
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (selectedIcon == icon) KachingaGreenLight
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                selectedIcon = icon
                                                showIconPicker = false
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(icon, fontSize = 18.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    // Error
                    if (uiState.error != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFF0F0)
                            )
                        ) {
                            Text(
                                uiState.error!!,
                                color = KachingaRed,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Submit
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (uiState.isLoading) KachingaGreen.copy(alpha = 0.5f)
                                else KachingaGreen
                            )
                            .clickable(enabled = !uiState.isLoading) {
                                billViewModel.addBill(
                                    name = billName,
                                    amount = amount,
                                    dueDay = dueDay,
                                    category = selectedCategory,
                                    icon = selectedIcon,
                                    onSuccess = onSuccess
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Add Bill Reminder",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
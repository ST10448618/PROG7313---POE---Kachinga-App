package com.example.prog7313_poe_kachinga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.SavingsViewModel

@Composable
fun AddSavingsGoalScreen(
    onBackClick: () -> Unit,
    savingsViewModel: SavingsViewModel,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("💰") }
    var error by remember { mutableStateOf<String?>(null) }

    val goalIcons = listOf("💰","🚗","🏠","✈","📱","🎓","💍","🏋","🌴","🎮")

    AnimatedScreen {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0FAF4))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 40.dp)
            ) {
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
                            Icons.Filled.ArrowBack, contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp).clickable { onBackClick() }
                                .wrapContentSize(Alignment.Center)
                        )
                        Text(
                            "New Savings Goal", fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = Color.White,
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
                    // Icon preview
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(20.dp))
                            .background(KachingaGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedIcon, fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Icon selector row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        goalIcons.forEach { icon ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedIcon == icon) KachingaGreen
                                        else Color(0xFFF0FAF4)
                                    )
                                    .clickable { selectedIcon = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 18.sp)
                            }
                        }
                    }

                    FormLabel("Goal Name")
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("e.g. New Car", color = TextHint) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = kachingaTextFieldColors()
                    )

                    FormLabel("Target Amount (R)")
                    OutlinedTextField(
                        value = targetAmount,
                        onValueChange = { targetAmount = it },
                        placeholder = { Text("0.00", color = TextHint) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal),
                        colors = kachingaTextFieldColors()
                    )

                    if (error != null) {
                        Text(error!!, color = KachingaRed, fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 12.dp))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(KachingaGreen)
                            .clickable {
                                val target = targetAmount.toDoubleOrNull()
                                when {
                                    name.isBlank() -> {
                                        error = "Goal name is required"
                                        return@clickable
                                    }
                                    target == null || target <= 0 -> {
                                        error = "Enter a valid target amount"
                                        return@clickable
                                    }
                                }
                                savingsViewModel.addGoal(name, selectedIcon, target!!)
                                onSuccess()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Create Goal", fontSize = 15.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
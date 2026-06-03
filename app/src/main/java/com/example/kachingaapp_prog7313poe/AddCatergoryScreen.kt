package com.example.kachingaapp_prog7313poe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kachingaapp_prog7313poe.ui.theme.*
import com.example.kachingaapp_prog7313poe.viewmodel.CategoryViewModel

@Composable
fun AddCategoryScreen(
    onBackClick: () -> Unit,
    categoryViewModel: CategoryViewModel,
    onSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("🍔") }
    var isExpense by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val icons = listOf(
        "🍔","🚌","💊","✈","🏠","🚗","🛒","🏢","🎁","🎬",
        "💵","💰","📱","🎓","💡","🏋","🐾","🎮","🍕","☕",
        "🎵","📚","🏥","🌿","🚿","🛍","🔧","🎨","⚽","🚿"
    )

    AnimatedScreen {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0FAF4))) {
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
                            Icons.Filled.ArrowBack, contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp).clickable { onBackClick() }
                                .wrapContentSize(Alignment.Center)
                        )
                        Text(
                            "Add Category", fontSize = 18.sp,
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
                    // Type toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFFF0FAF4))
                            .padding(4.dp)
                            .padding(bottom = 20.dp)
                    ) {
                        listOf(true to "Expense", false to "Income").forEach { (type, label) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f).height(44.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(
                                        when {
                                            isExpense == type && type -> KachingaRed
                                            isExpense == type && !type -> KachingaGreen
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { isExpense = type },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpense == type) Color.White
                                    else TextSecondary)
                            }
                        }
                    }

                    // Preview
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(16.dp))
                            .background(KachingaGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedIcon, fontSize = 32.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name
                    FormLabel("Category Name")
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("e.g. Gym", color = TextHint) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = kachingaTextFieldColors()
                    )

                    // Icon picker
                    FormLabel("Choose Icon")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(icons) { icon ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selectedIcon == icon) KachingaGreenLight
                                        else Color(0xFFF7F7F7)
                                    )
                                    .then(
                                        if (selectedIcon == icon)
                                            Modifier.border(2.dp, KachingaGreen,
                                                RoundedCornerShape(10.dp))
                                        else Modifier
                                    )
                                    .clickable { selectedIcon = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 20.sp)
                            }
                        }
                    }

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
                                if (name.isBlank()) {
                                    error = "Category name is required"
                                    return@clickable
                                }
                                categoryViewModel.addCategory(name, selectedIcon, isExpense)
                                onSuccess()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Save Category", fontSize = 15.sp,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
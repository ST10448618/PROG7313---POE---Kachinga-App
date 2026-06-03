package com.example.prog7313_poe_kachinga

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prog7313_poe_kachinga.components.ImagePickerSection
import com.example.prog7313_poe_kachinga.data.entity.Category
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.CategoryViewModel
import com.example.prog7313_poe_kachinga.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddTransactionScreen(
    onBackClick: () -> Unit,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var imagePath by remember { mutableStateOf("") }

    // Date and time state
    val now = remember { Calendar.getInstance() }
    var selectedDateMillis by remember { mutableLongStateOf(now.timeInMillis) }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    val categories by categoryViewModel.allCategories.collectAsState()
    val uiState by transactionViewModel.uiState.collectAsState()
    val filteredCategories = categories.filter { it.isExpense == isExpense }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val displayDate = dateFormat.format(Date(selectedDateMillis))

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            transactionViewModel.clearMessages()
            onSuccess()
        }
    }

    // Date picker
    fun showDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance()
                newCal.set(year, month, dayOfMonth, 0, 0, 0)
                newCal.set(Calendar.MILLISECOND, 0)
                selectedDateMillis = newCal.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Time picker
    fun showTimePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val formatted = String.format("%02d:%02d", hour, minute)
                if (isStart) startTime = formatted else endTime = formatted
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
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
                            "Add Transaction",
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

                    // Expense / Income toggle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0xFFF0FAF4))
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(true to "Expense", false to "Income").forEach { (type, label) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(
                                            when {
                                                isExpense == type && type -> KachingaRed
                                                isExpense == type && !type -> KachingaGreen
                                                else -> Color.Transparent
                                            }
                                        )
                                        .clickable {
                                            isExpense = type
                                            selectedCategory = null
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        label,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isExpense == type) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))


                    // Title
                    FormLabel("Title")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Grocery run", color = TextHint) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = kachingaTextFieldColors()
                    )

                    // Description
                    FormLabel("Description")
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("What was this for?", color = TextHint) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3,
                        colors = kachingaTextFieldColors()
                    )

                    // Amount
                    FormLabel("Amount (R)")
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        placeholder = { Text("0.00", color = TextHint) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = kachingaTextFieldColors()
                    )

                    // Date picker
                    FormLabel("Date")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFF7F7F7))
                            .clickable { showDatePicker() }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .padding(bottom = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.CalendarToday,
                                contentDescription = null,
                                tint = KachingaGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                displayDate,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Start and end time
                    FormLabel("Time")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Start time
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF7F7F7))
                                .clickable { showTimePicker(true) }
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = KachingaGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        "Start",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        startTime.ifBlank { "Tap to set" },
                                        fontSize = 13.sp,
                                        color = if (startTime.isBlank()) TextHint else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // End time
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF7F7F7))
                                .clickable { showTimePicker(false) }
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = KachingaGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        "End",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                    Text(
                                        endTime.ifBlank { "Tap to set" },
                                        fontSize = 13.sp,
                                        color = if (endTime.isBlank()) TextHint else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Category picker
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
                                text = if (selectedCategory != null)
                                    "${selectedCategory!!.icon}  ${selectedCategory!!.name}"
                                else "Select a category",
                                fontSize = 14.sp,
                                color = if (selectedCategory != null) TextPrimary else TextHint,
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
                            if (filteredCategories.isEmpty()) {
                                Text(
                                    "No categories. Tap Add New in Categories screen.",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            } else {
                                filteredCategories.forEach { category ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (selectedCategory?.id == category.id)
                                                    KachingaGreenLight
                                                else Color.Transparent
                                            )
                                            .clickable {
                                                selectedCategory = category
                                                showCategoryPicker = false
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(category.icon, fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            category.name,
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Note
                    FormLabel("Note (optional)")
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Add a note...", color = TextHint) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3,
                        colors = kachingaTextFieldColors()
                    )

                    // Image picker
                    ImagePickerSection(
                        imagePath = imagePath,
                        onImageSelected = { path -> imagePath = path },
                        onImageRemoved = { imagePath = "" }
                    )

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

                    // Submit button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedCategory == null || uiState.isLoading)
                                    KachingaGreen.copy(alpha = 0.5f)
                                else KachingaGreen
                            )
                            .clickable(
                                enabled = selectedCategory != null && !uiState.isLoading
                            ) {
                                transactionViewModel.addTransaction(
                                    title = title.ifBlank { selectedCategory!!.name },
                                    amount = amount,
                                    categoryId = selectedCategory!!.id,
                                    categoryName = selectedCategory!!.name,
                                    categoryIcon = selectedCategory!!.icon,
                                    isExpense = isExpense,
                                    note = note,
                                    description = description,
                                    startTime = startTime,
                                    endTime = endTime,
                                    date = selectedDateMillis,
                                    imagePath = imagePath,
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
                                if (selectedCategory == null) "Select a category first"
                                else "Add Transaction",
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

@Composable
fun FormLabel(text: String) {
    Text(
        text,
        fontSize = 13.sp,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun kachingaTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = KachingaGreen,
    unfocusedBorderColor = Divider,
    focusedContainerColor = Color(0xFFF7F7F7),
    unfocusedContainerColor = Color(0xFFF7F7F7)
)
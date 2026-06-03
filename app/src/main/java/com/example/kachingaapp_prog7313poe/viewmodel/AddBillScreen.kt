package com.example.prog7313_poe_kachinga

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
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.BillViewModel
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


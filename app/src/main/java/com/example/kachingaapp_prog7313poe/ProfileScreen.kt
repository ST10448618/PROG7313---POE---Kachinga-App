package com.example.kachingaapp_prog7313poe

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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kachingaapp_prog7313poe.navigation.NavRoutes
import com.example.kachingaapp_prog7313poe.ui.theme.*
import com.example.kachingaapp_prog7313poe.viewmodel.AuthViewModel
import com.example.kachingaapp_prog7313poe.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel(),
    onNavigate: ((String) -> Unit)? = null
) {
    val profileState by profileViewModel.profileState.collectAsState()
    val isEditingPersonal by profileViewModel.isEditingPersonal.collectAsState()
    val isEditingFinancial by profileViewModel.isEditingFinancial.collectAsState()

    // Reload every time screen is visited
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    var editName by remember(profileState.fullName) { mutableStateOf(profileState.fullName) }
    var editEmail by remember(profileState.email) { mutableStateOf(profileState.email) }
    var editPhone by remember(profileState.phone) { mutableStateOf(profileState.phone) }
    var editSalary by remember(profileState.monthlySalary) {
        mutableStateOf(if (profileState.monthlySalary > 0) profileState.monthlySalary.toString() else "")
    }
    var editSavingsPct by remember(profileState.savingsTargetPct) {
        mutableStateOf(if (profileState.savingsTargetPct > 0) profileState.savingsTargetPct.toInt().toString() else "")
    }
    var editBudget by remember(profileState.monthlyBudget) {
        mutableStateOf(if (profileState.monthlyBudget > 0) profileState.monthlyBudget.toString() else "")
    }
    var editMinGoal by remember(profileState.minMonthlyGoal) {
        mutableStateOf(if (profileState.minMonthlyGoal > 0) profileState.minMonthlyGoal.toString() else "")
    }
    var editMaxGoal by remember(profileState.maxMonthlyGoal) {
        mutableStateOf(if (profileState.maxMonthlyGoal > 0) profileState.maxMonthlyGoal.toString() else "")
    }
    var editCurrency by remember(profileState.currency) { mutableStateOf(profileState.currency) }

    var budgetAlerts by remember { mutableStateOf(true) }
    var savingsReminders by remember { mutableStateOf(true) }
    var weeklyReports by remember { mutableStateOf(false) }
    var achievementAlerts by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val salary = editSalary.toDoubleOrNull() ?: 0.0
    val savingsPct = editSavingsPct.toDoubleOrNull() ?: 0.0
    val savingsAmount = salary * (savingsPct / 100.0)
    val spendingAmount = salary - savingsAmount

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    authViewModel.logout {
                        onNavigate?.invoke(NavRoutes.LAUNCH)
                    }
                }) {
                    Text("Log Out", color = KachingaRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
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
                    .padding(bottom = 90.dp)
            ) {
                // Green Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KachingaGreen)
                        .padding(horizontal = 20.dp)
                        .padding(top = 48.dp, bottom = 32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp),
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
                                "Profile",
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
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Notifications,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profileState.fullName
                                    .split(" ")
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                    .joinToString(""),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            profileState.fullName.ifBlank { "User" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            profileState.email,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStat(
                                label = "Monthly Income",
                                value = if (salary > 0) "R ${"%,.0f".format(salary)}" else "Not set"
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )
                            ProfileStat(
                                label = "Savings Target",
                                value = if (savingsPct > 0) "${savingsPct.toInt()}%" else "Not set"
                            )
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(40.dp)
                                    .background(Color.White.copy(alpha = 0.3f))
                            )
                            ProfileStat(
                                label = "Monthly Save",
                                value = if (savingsAmount > 0) "R ${"%,.0f".format(savingsAmount)}" else "R 0"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Personal Info Card
                ProfileCard(
                    title = "Personal Information",
                    icon = Icons.Filled.Person,
                    isEditing = isEditingPersonal,
                    onEditToggle = { profileViewModel.toggleEditingPersonal() }
                ) {
                    if (isEditingPersonal) {
                        ProfileEditField("Full Name", editName, { editName = it })
                        ProfileEditField("Email", editEmail, { editEmail = it }, keyboardType = KeyboardType.Email)
                        ProfileEditField("Phone Number", editPhone, { editPhone = it }, keyboardType = KeyboardType.Phone)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(KachingaGreen)
                                .clickable {
                                    profileViewModel.savePersonalInfo(editName, editEmail, editPhone)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Save Changes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        ProfileInfoRow(Icons.Filled.Person, "Full Name", profileState.fullName.ifBlank { "Not set" })
                        ProfileInfoRow(Icons.Filled.Email, "Email", profileState.email.ifBlank { "Not set" })
                        ProfileInfoRow(Icons.Filled.Phone, "Phone", profileState.phone.ifBlank { "Not set" })
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Financial Settings Card
                ProfileCard(
                    title = "Financial Settings",
                    icon = Icons.Filled.AttachMoney,
                    isEditing = isEditingFinancial,
                    onEditToggle = { profileViewModel.toggleEditingFinancial() }
                ) {
                    if (isEditingFinancial) {
                        ProfileEditField("Monthly Salary (R)", editSalary, { editSalary = it }, keyboardType = KeyboardType.Decimal)
                        ProfileEditField(
                            "Savings Target (% of salary)", editSavingsPct,
                            { if (it.toIntOrNull() in 1..100 || it.isEmpty()) editSavingsPct = it },
                            keyboardType = KeyboardType.Number
                        )
                        ProfileEditField("Monthly Spending Budget (R)", editBudget, { editBudget = it }, keyboardType = KeyboardType.Decimal)
                        ProfileEditField(
                            label = "Minimum Monthly Spend Goal (R)",
                            value = editMinGoal,
                            onValueChange = { editMinGoal = it },
                            keyboardType = KeyboardType.Decimal
                        )
                        ProfileEditField(
                            label = "Maximum Monthly Spend Goal (R)",
                            value = editMaxGoal,
                            onValueChange = { editMaxGoal = it },
                            keyboardType = KeyboardType.Decimal
                        )

                        Text("Currency", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("ZAR (R)", "USD ($)", "EUR (€)", "GBP (£)").forEach { option ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (editCurrency == option) KachingaGreen else Color(0xFFF0FAF4))
                                        .clickable { editCurrency = option }
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        option, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        color = if (editCurrency == option) Color.White else KachingaGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(KachingaGreen)
                                .clickable {
                                    profileViewModel.saveFinancialSettings(
                                        salary = editSalary.toDoubleOrNull() ?: 0.0,
                                        savingsPct = editSavingsPct.toDoubleOrNull() ?: 0.0,
                                        budget = editBudget.toDoubleOrNull() ?: 0.0,
                                        currency = editCurrency,
                                        minGoal = editMinGoal.toDoubleOrNull() ?: 0.0,
                                        maxGoal = editMaxGoal.toDoubleOrNull() ?: 0.0
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Save Changes", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        if (salary > 0) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF0FAF4))
                                    .padding(16.dp)
                                    .padding(bottom = 12.dp)
                            ) {
                                Text("Salary Breakdown", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                    color = TextPrimary, modifier = Modifier.padding(bottom = 12.dp))
                                val savePct = (savingsAmount / salary).toFloat().coerceIn(0f, 1f)
                                val spendPct = 1f - savePct
                                Row(modifier = Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp))) {
                                    Box(modifier = Modifier.weight(savePct.coerceAtLeast(0.01f)).fillMaxHeight().background(KachingaGreen))
                                    Box(modifier = Modifier.weight(spendPct.coerceAtLeast(0.01f)).fillMaxHeight().background(Color(0xFF5DCAA5)))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(KachingaGreen))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Save  R ${"%,.0f".format(savingsAmount)}", fontSize = 12.sp, color = TextSecondary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF5DCAA5)))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Spend  R ${"%,.0f".format(spendingAmount)}", fontSize = 12.sp, color = TextSecondary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        ProfileInfoRow(Icons.Filled.AttachMoney, "Monthly Salary",
                            if (profileState.monthlySalary > 0) "R ${"%,.2f".format(profileState.monthlySalary)}" else "Not set")
                        ProfileInfoRow(Icons.Filled.Savings, "Savings Target",
                            if (profileState.savingsTargetPct > 0) "${profileState.savingsTargetPct.toInt()}% = R ${"%,.2f".format(savingsAmount)}" else "Not set")
                        ProfileInfoRow(Icons.Filled.ShoppingCart, "Spending Budget",
                            if (profileState.monthlyBudget > 0) "R ${"%,.2f".format(profileState.monthlyBudget)}" else "Not set")
                        ProfileInfoRow(
                            Icons.Filled.TrendingDown,
                            "Min Monthly Goal",
                            if (profileState.minMonthlyGoal > 0)
                                "R ${"%,.2f".format(profileState.minMonthlyGoal)}"
                            else "Not set"
                        )
                        ProfileInfoRow(
                            Icons.Filled.TrendingUp,
                            "Max Monthly Goal",
                            if (profileState.maxMonthlyGoal > 0)
                                "R ${"%,.2f".format(profileState.maxMonthlyGoal)}"
                            else "Not set"
                        )
                        ProfileInfoRow(Icons.Filled.CurrencyExchange, "Currency", profileState.currency)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notifications Card
                ProfileCard(title = "Notifications", icon = Icons.Filled.Notifications, isEditing = false, onEditToggle = null) {
                    NotificationToggle(Icons.Filled.Warning, "Budget Alerts", "Alert when nearing monthly budget", budgetAlerts) { budgetAlerts = it }
                    NotificationToggle(Icons.Filled.Savings, "Savings Reminders", "Weekly reminder to add to savings", savingsReminders) { savingsReminders = it }
                    NotificationToggle(Icons.Filled.BarChart, "Weekly Reports", "Summary of your weekly spending", weeklyReports) { weeklyReports = it }
                    NotificationToggle(Icons.Filled.EmojiEvents, "Achievement Alerts", "Notify when a badge is earned", achievementAlerts) { achievementAlerts = it }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Account Card
                ProfileCard(title = "Account", icon = Icons.Filled.ManageAccounts, isEditing = false, onEditToggle = null) {
                    AccountActionRow(icon = Icons.Filled.Lock, label = "Change Password", iconTint = KachingaGreen)
                    AccountActionRow(icon = Icons.Filled.Download, label = "Export My Data", iconTint = KachingaGreen)
                    AccountActionRow(icon = Icons.Filled.DeleteForever, label = "Delete Account", iconTint = KachingaRed, labelColor = KachingaRed)
                    AccountActionRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        label = "Log Out",
                        iconTint = KachingaRed,
                        labelColor = KachingaRed,
                        onClick = { showLogoutDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "Kachinga v1.0.0", fontSize = 12.sp, color = TextSecondary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            BottomNavBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                selectedIndex = 4,
                onNavigate = onNavigate
            )
        }
    }
}

// ── Reusable components ───────────────────────────────────────────

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.75f), textAlign = TextAlign.Center)
    }
}

@Composable
fun ProfileCard(
    title: String,
    icon: ImageVector,
    isEditing: Boolean,
    onEditToggle: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(KachingaGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = KachingaGreen, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
            if (onEditToggle != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isEditing) KachingaGreen else Color(0xFFF0FAF4))
                        .clickable { onEditToggle() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        if (isEditing) "Cancel" else "Edit", fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEditing) Color.White else KachingaGreen
                    )
                }
            }
        }
        content()
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = KachingaGreen, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
    HorizontalDivider(color = Divider, thickness = 0.5.dp)
}

@Composable
fun ProfileEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Text(label, fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KachingaGreen,
            unfocusedBorderColor = Divider,
            focusedContainerColor = Color(0xFFF7F7F7),
            unfocusedContainerColor = Color(0xFFF7F7F7)
        )
    )
}

@Composable
fun NotificationToggle(
    icon: ImageVector, title: String, subtitle: String,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(KachingaGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = KachingaGreen, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Switch(
            checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, checkedTrackColor = KachingaGreen,
                uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFCCCCCC)
            )
        )
    }
    HorizontalDivider(color = Divider, thickness = 0.5.dp)
}

@Composable
fun AccountActionRow(
    icon: ImageVector, label: String,
    iconTint: Color = KachingaGreen, labelColor: Color = TextPrimary,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, fontSize = 14.sp, color = labelColor, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
    }
    HorizontalDivider(color = Divider, thickness = 0.5.dp)
}
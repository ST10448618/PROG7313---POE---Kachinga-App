package com.example.prog7313_poe_kachinga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prog7313_poe_kachinga.ui.theme.*
import com.example.prog7313_poe_kachinga.viewmodel.AuthViewModel

@Composable
fun CreateAccountScreen(
    onSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    val uiState by authViewModel.uiState.collectAsState()
    val navigateAfterRegister by authViewModel.navigateAfterRegister.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // StateFlow navigation — safe from background thread
    LaunchedEffect(navigateAfterRegister) {
        if (navigateAfterRegister) {
            authViewModel.onNavigatedAfterRegister()
            onSignUpClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        Text(
            text = "Create Account",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        RegisterField("Full Name", fullName, { fullName = it; authViewModel.clearError() })
        RegisterField(
            "Email", email,
            { email = it; authViewModel.clearError() },
            keyboardType = KeyboardType.Email
        )
        RegisterField("Mobile Number", mobile, { mobile = it },
            keyboardType = KeyboardType.Phone)
        RegisterField("Date of Birth", dob, { dob = it },
            hint = "DD / MM / YYYY")
        RegisterField(
            "Password", password,
            { password = it; authViewModel.clearError() },
            isPassword = true,
            hint = "Min 6 characters"
        )
        RegisterField(
            "Confirm Password", confirmPassword,
            { confirmPassword = it; authViewModel.clearError() },
            isPassword = true,
            hint = "Repeat password"
        )

        Text(
            text = "By continuing, you agree to our Terms and Privacy Policy",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (uiState.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0))
            ) {
                Text(
                    text = uiState.error ?: "",
                    color = KachingaRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (uiState.isLoading) KachingaGreen.copy(alpha = 0.6f)
                    else KachingaGreen
                )
                .clickable(enabled = !uiState.isLoading) {
                    authViewModel.register(
                        fullName = fullName,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword
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
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already have an account? Log In",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLoginClick() },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = label,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Text(
        text = label,
        fontSize = 13.sp,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 6.dp)
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = TextHint) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = KachingaGreen,
            unfocusedBorderColor = Divider,
            focusedContainerColor = Color(0xFFF7F7F7),
            unfocusedContainerColor = Color(0xFFF7F7F7)
        )
    )
}
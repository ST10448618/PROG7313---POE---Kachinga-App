package com.example.kachingaapp_prog7313poe

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kachingaapp_prog7313poe.ui.theme.*
import com.example.kachingaapp_prog7313poe.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    val uiState by authViewModel.uiState.collectAsState()
    val navigateToHome by authViewModel.navigateToHome.collectAsState()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Navigation is triggered by StateFlow — safe from any thread
    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            authViewModel.onNavigatedToHome()
            onLoginClick()
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
        // Logo row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.kachinga_logo),
                contentDescription = "Kachinga Logo",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "KACHINGA",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = KachingaGreen
            )
        }

        Text(
            text = "Welcome",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 28.dp)
        )

        // Email
        Text(
            text = "Email",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (uiState.error != null) authViewModel.clearError()
            },
            placeholder = { Text("example@example.com", color = TextHint) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            singleLine = true,
            isError = uiState.error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KachingaGreen,
                unfocusedBorderColor = Divider,
                errorBorderColor = KachingaRed,
                focusedContainerColor = Color(0xFFF7F7F7),
                unfocusedContainerColor = Color(0xFFF7F7F7),
                errorContainerColor = Color(0xFFFFF0F0)
            )
        )
// Password
        Text(
            text = "Password",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (uiState.error != null) authViewModel.clearError()
            },
            placeholder = { Text("••••••••", color = TextHint) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(10.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    authViewModel.login(email.trim(), password.trim())
                }
            ),
            singleLine = true,
            isError = uiState.error != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KachingaGreen,
                unfocusedBorderColor = Divider,
                errorBorderColor = KachingaRed,
                focusedContainerColor = Color(0xFFF7F7F7),
                unfocusedContainerColor = Color(0xFFF7F7F7),
                errorContainerColor = Color(0xFFFFF0F0)
            )
        )

        Text(
            text = "Forgot Password?",
            fontSize = 13.sp,
            color = KachingaGreen,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.End
        )

        // Error card
        if (uiState.error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF0F0)
                )
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

        // Login button — no lambda passed to ViewModel
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
                    focusManager.clearFocus()
                    authViewModel.login(email.trim(), password.trim())
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
                    text = "Log In",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "👆", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Use Fingerprint to Access",
                fontSize = 13.sp,
                color = KachingaGreen
            )
        }

        Text(
            text = "Don't have an account? Sign Up",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSignUpClick() },
            textAlign = TextAlign.Center
        )
    }
}
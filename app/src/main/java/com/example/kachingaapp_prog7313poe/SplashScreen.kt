package com.example.kachingaapp_prog7313poe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.kachingaapp_prog7313poe.ui.theme.KachingaGreen
import com.example.kachingaapp_prog7313poe.ui.theme.KachingaGreenDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {

    LaunchedEffect(Unit) {
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.kachinga_logo),
                contentDescription = "Kachinga Logo",
                modifier = Modifier.size(160.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "KACHINGA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = KachingaGreenDark,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Smart Finance, Smarter Life",
                fontSize = 14.sp,
                color = KachingaGreenDark.copy(alpha = 0.8f)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}
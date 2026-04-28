package com.example.prog7313_poe_kachinga

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prog7313_poe_kachinga.navigation.NavRoutes
import com.example.prog7313_poe_kachinga.ui.theme.KachingaGreen
import com.example.prog7313_poe_kachinga.ui.theme.TextSecondary

data class NavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onNavigate: ((String) -> Unit)? = null
) {
    val items = listOf(
        NavItem(Icons.Filled.Home,    "Home",    NavRoutes.HOME),
        NavItem(Icons.Filled.BarChart, "Stats",  NavRoutes.CALENDAR),
        NavItem(Icons.Filled.Add,     "Add",     NavRoutes.ADD_TRANSACTION),
        NavItem(Icons.Filled.Savings, "Savings", NavRoutes.SAVINGS),
        NavItem(Icons.Filled.Person,  "Profile", NavRoutes.PROFILE)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color.White)
            .navigationBarsPadding()
            .height(72.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            if (index == 2) {
                // Centre FAB
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(KachingaGreen)
                        .clickable { onNavigate?.invoke(item.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Transaction",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                val isSelected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate?.invoke(item.route) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) KachingaGreen
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        color = if (isSelected) KachingaGreen else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold
                        else FontWeight.Normal
                    )
                }
            }
        }
    }
}
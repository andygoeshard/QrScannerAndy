package com.andy.qrcamtest.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home: BottomNavItem("home", Icons.Default.Home, "Home")
    object History: BottomNavItem("history", Icons.Default.DateRange, "History")
}
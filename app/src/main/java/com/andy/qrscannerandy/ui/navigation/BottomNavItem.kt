package com.andy.qrscannerandy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.andy.qrscannerandy.R

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: Int) {
    object Home: BottomNavItem("home", icon = Icons.Default.Home, R.string.home)
    object History: BottomNavItem("history", icon = Icons.Default.DateRange,R.string.history)
}
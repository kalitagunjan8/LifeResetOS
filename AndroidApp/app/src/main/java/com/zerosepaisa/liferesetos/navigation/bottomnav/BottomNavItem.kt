package com.zerosepaisa.liferesetos.navigation.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        icon = Icons.Outlined.Home
    )

    object Journey : BottomNavItem(
        route = "com/zerosepaisa/liferesetos/feature/journey",
        title = "Journey",
        icon = Icons.Outlined.TrackChanges
    )

    object Focus : BottomNavItem(
        route = "focus",
        title = "Focus",
        icon = Icons.Outlined.Timer
    )

    object Profile : BottomNavItem(
        route = "profile",
        title = "Profile",
        icon = Icons.Outlined.Person
    )
}
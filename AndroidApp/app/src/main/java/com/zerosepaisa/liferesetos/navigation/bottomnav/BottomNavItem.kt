package com.zerosepaisa.liferesetos.navigation.bottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.zerosepaisa.liferesetos.navigation.Routes

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        route = Routes.HOME,
        title = "Home",
        icon = Icons.Outlined.Home
    )

    object Journey : BottomNavItem(
        route = Routes.JOURNEY,
        title = "Journey",
        icon = Icons.Outlined.TrackChanges
    )

    object Focus : BottomNavItem(
        route = Routes.FOCUS,
        title = "Focus",
        icon = Icons.Outlined.Timer
    )

    object Profile : BottomNavItem(
        route = Routes.PROFILE,
        title = "Profile",
        icon = Icons.Outlined.Person
    )
}
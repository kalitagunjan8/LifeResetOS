package com.zerosepaisa.liferesetos.navigation.bottomnav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    isNavigationBlocked: Boolean = false,
    onBlockedNavigationAttempt: (String) -> Unit = {}
) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Journey,
        BottomNavItem.Focus,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {

        items.forEach { item ->

            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            NavigationBarItem(

                selected = selected,

                onClick = {
                    if (!selected) {
                        if (isNavigationBlocked) {
                            onBlockedNavigationAttempt(item.route)
                        } else {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },

                label = {
                    Text(item.title)
                }

            )

        }

    }

}
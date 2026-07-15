package com.zerosepaisa.liferesetos.navigation.bottomnav

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomNavigationBar(

    selectedRoute: String,

    onItemClick: (String) -> Unit

) {

    val items = listOf(

        BottomNavItem.Home,
        BottomNavItem.Journey,
        BottomNavItem.Focus,
        BottomNavItem.Profile

    )

    NavigationBar {

        items.forEach { item ->

            NavigationBarItem(

                selected = selectedRoute == item.route,

                onClick = {
                    onItemClick(item.route)
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
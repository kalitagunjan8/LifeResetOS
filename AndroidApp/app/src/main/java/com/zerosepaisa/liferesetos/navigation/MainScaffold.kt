package com.zerosepaisa.liferesetos.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import com.zerosepaisa.liferesetos.feature.focus.FocusScreen
import com.zerosepaisa.liferesetos.feature.home.HomeScreen
import com.zerosepaisa.liferesetos.feature.journey.JourneyScreen
import com.zerosepaisa.liferesetos.feature.profile.ProfileScreen
import com.zerosepaisa.liferesetos.navigation.bottomnav.BottomNavItem
import com.zerosepaisa.liferesetos.navigation.bottomnav.BottomNavigationBar

@Composable
fun MainScaffold() {

    var currentRoute by remember {
        mutableStateOf(BottomNavItem.Home.route)
    }

    Scaffold(

        bottomBar = {
            BottomNavigationBar(
                selectedRoute = currentRoute,
                onItemClick = {
                    currentRoute = it
                }
            )
        }

    ) { innerPadding ->

        when (currentRoute) {

            BottomNavItem.Home.route -> {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Journey.route -> {
                JourneyScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Focus.route -> {
                FocusScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            BottomNavItem.Profile.route -> {
                ProfileScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }

        }

    }

}
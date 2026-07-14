package com.zerosepaisa.liferesetos.navigation

import androidx.compose.runtime.Composable
import com.zerosepaisa.liferesetos.navigation.Routes
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zerosepaisa.liferesetos.feature.home.HomeScreen
import com.zerosepaisa.liferesetos.feature.onboarding.WelcomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {

        composable(Routes.WELCOME) {

            WelcomeScreen(
                onBeginClick = {
                    navController.navigate(Routes.HOME)
                }
            )

        }

        composable(Routes.HOME) {

            HomeScreen()

        }

    }
}
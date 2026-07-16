package com.zerosepaisa.liferesetos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zerosepaisa.liferesetos.feature.home.HomeScreen
import com.zerosepaisa.liferesetos.feature.onboarding.WelcomeScreen
import com.zerosepaisa.liferesetos.navigation.Routes
import com.zerosepaisa.liferesetos.viewmodel.MainViewModel
import com.zerosepaisa.liferesetos.feature.mission.MissionScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            LaunchedEffect(isFirstLaunch) {
                val knownValue = isFirstLaunch
                if (knownValue != null) {
                    val destination = if (knownValue) Routes.WELCOME else Routes.HOME
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) {
                            inclusive = true
                        }
                    }
                }
            }
        }

        composable(Routes.WELCOME) {

            WelcomeScreen(
                onBeginClick = {
                    navController.navigate(Routes.MISSION)
                }
            )


        }
        composable(Routes.MISSION) {
            MissionScreen(
                onContinue = {

                    viewModel.completeOnboarding()

                    navController.navigate(Routes.HOME) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.HOME) {

            MainScaffold()

        }

    }
}
package com.zerosepaisa.liferesetos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zerosepaisa.liferesetos.feature.focus.FocusScreen
import com.zerosepaisa.liferesetos.feature.home.HomeScreen
import com.zerosepaisa.liferesetos.feature.goals.GoalsScreen
import com.zerosepaisa.liferesetos.feature.goaldetail.GoalDetailScreen
import com.zerosepaisa.liferesetos.feature.journey.JourneyScreen
import com.zerosepaisa.liferesetos.feature.profile.ProfileScreen
import com.zerosepaisa.liferesetos.feature.todaysactions.TodaysActionsScreen
import com.zerosepaisa.liferesetos.feature.progress.ProgressScreen
import com.zerosepaisa.liferesetos.feature.onboarding.WelcomeScreen
import com.zerosepaisa.liferesetos.navigation.bottomnav.BottomNavItem
import com.zerosepaisa.liferesetos.viewmodel.MainViewModel
import com.zerosepaisa.liferesetos.feature.mission.MissionScreen
import com.zerosepaisa.liferesetos.feature.backup.BackupScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {

    val viewModel: MainViewModel = viewModel()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()
    var isFocusSessionRunning by remember { mutableStateOf(false) }
    var endFocusSession by remember { mutableStateOf<() -> Unit>({}) }
    var pendingBlockedRoute by remember { mutableStateOf<String?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    )

    {

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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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

        composable(BottomNavItem.Home.route) {
            MainScaffold(
                navController = navController,
                isNavigationBlocked = isFocusSessionRunning,
                onBlockedNavigationAttempt = { route -> pendingBlockedRoute = route }
            ) { modifier ->
                HomeScreen(
                    modifier = modifier,
                    onTodaysActionsClick = {
                        navController.navigate(Routes.TODAYS_ACTIONS)
                    },
                    onFocusScoreClick = {
                        navController.navigate(Routes.PROGRESS)
                    },
                    onActiveGoalsClick = {
                        navController.navigate(BottomNavItem.Journey.route)
                    }
                )
            }
        }

        composable(BottomNavItem.Journey.route) {
            MainScaffold(
                navController = navController,
                isNavigationBlocked = isFocusSessionRunning,
                onBlockedNavigationAttempt = { route -> pendingBlockedRoute = route }
            ) { modifier ->
                JourneyScreen(
                    modifier = modifier,
                    onAddGoalClick = {
                        navController.navigate(Routes.goalsRoute())
                    },
                    onGoalClick = { goalId ->
                        navController.navigate(Routes.goalDetailRoute(goalId))
                    }
                )
            }
        }

        composable(BottomNavItem.Focus.route) {
            MainScaffold(
                navController = navController,
                isNavigationBlocked = isFocusSessionRunning,
                onBlockedNavigationAttempt = { route -> pendingBlockedRoute = route }
            ) { modifier ->
                FocusScreen(
                    modifier = modifier,
                    onGoToTodaysActions = {
                        navController.navigate(Routes.TODAYS_ACTIONS)
                    },
                    onRunningStateChanged = { running -> isFocusSessionRunning = running },
                    onRegisterEndSession = { endFocusSession = it }
                )
            }
        }


        composable(Routes.BACKUP) {
            BackupScreen()
        }

        composable(BottomNavItem.Profile.route) {
            MainScaffold(
                navController = navController,
                isNavigationBlocked = isFocusSessionRunning,
                onBlockedNavigationAttempt = { route -> pendingBlockedRoute = route }
            ) { modifier ->
                ProfileScreen(
                    modifier = modifier,
                    onBackupClick = {
                        navController.navigate(Routes.BACKUP)
                    }
                )
            }
        }

        composable(
            route = Routes.GOALS,
            arguments = listOf(
                navArgument(Routes.GOAL_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->

            val goalId = backStackEntry.arguments?.getLong(Routes.GOAL_ID_ARG) ?: -1L

            GoalsScreen(
                goalId = if (goalId == -1L) null else goalId,
                onGoalSaved = {
                    navController.popBackStack()
                }
            )

        }

        composable(
            route = Routes.GOAL_DETAIL,
            arguments = listOf(
                navArgument(Routes.GOAL_ID_ARG) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val goalId = backStackEntry.arguments?.getLong(Routes.GOAL_ID_ARG) ?: return@composable

            GoalDetailScreen(
                goalId = goalId,
                onEditClick = { id ->
                    navController.navigate(Routes.goalsRoute(id))
                }
            )

        }

        composable(Routes.TODAYS_ACTIONS) {

            TodaysActionsScreen()

        }

        composable(Routes.PROGRESS) {

            ProgressScreen()

        }

    }
    pendingBlockedRoute?.let { route ->
        AlertDialog(
            onDismissRequest = { pendingBlockedRoute = null },
            title = { Text("Focus session in progress") },
            text = { Text("Leave anyway? This will end your current session.") },
            confirmButton = {
                Button(
                    onClick = {
                        endFocusSession()
                        val targetRoute = route
                        pendingBlockedRoute = null
                        navController.navigate(targetRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Leave")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { pendingBlockedRoute = null }) {
                    Text("Stay")
                }
            }
        )
    }
}
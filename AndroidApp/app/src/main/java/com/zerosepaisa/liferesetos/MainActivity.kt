package com.zerosepaisa.liferesetos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.zerosepaisa.liferesetos.navigation.AppNavigation
import com.zerosepaisa.liferesetos.notifications.NotificationChannels
import com.zerosepaisa.liferesetos.notifications.NotificationHelper
import com.zerosepaisa.liferesetos.ui.theme.LifeResetOSTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op: reminders simply won't show until granted */ }

    private var pendingDeepLinkRoute: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as LifeResetOSApplication).appContainer

        NotificationChannels.createAll(this)
        appContainer.notificationScheduler.scheduleAll()
        requestNotificationPermissionIfNeeded()

        pendingDeepLinkRoute = intent?.getStringExtra(NotificationHelper.EXTRA_DEEP_LINK_ROUTE)

        setContent {
            LifeResetOSTheme {
                val navController = rememberNavController()

                LaunchedEffect(pendingDeepLinkRoute) {
                    pendingDeepLinkRoute?.let { route ->
                        navController.navigate(route)
                        pendingDeepLinkRoute = null
                    }
                }

                AppNavigation(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingDeepLinkRoute = intent.getStringExtra(NotificationHelper.EXTRA_DEEP_LINK_ROUTE)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
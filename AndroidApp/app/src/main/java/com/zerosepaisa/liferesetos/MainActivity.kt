package com.zerosepaisa.liferesetos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.zerosepaisa.liferesetos.navigation.AppNavigation
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.zerosepaisa.liferesetos.ui.theme.LifeResetOSTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LifeResetOSTheme {
                AppNavigation()
            }
        }
    }
}
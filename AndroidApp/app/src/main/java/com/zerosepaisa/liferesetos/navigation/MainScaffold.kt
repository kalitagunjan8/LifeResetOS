package com.zerosepaisa.liferesetos.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.zerosepaisa.liferesetos.navigation.bottomnav.BottomNavigationBar

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }

}
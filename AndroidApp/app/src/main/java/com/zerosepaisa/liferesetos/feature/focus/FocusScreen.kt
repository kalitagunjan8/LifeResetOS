package com.zerosepaisa.liferesetos.feature.focus

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FocusScreen(
    modifier: Modifier = Modifier
)
{

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "Focus (Coming Soon)",
            style = MaterialTheme.typography.headlineMedium
        )

    }

}
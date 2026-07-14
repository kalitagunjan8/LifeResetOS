package com.zerosepaisa.liferesetos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = PrimaryVariant,
    tertiary = Accent,

    background = Background,
    surface = Surface,

    onPrimary = White,
    onSecondary = White,
    onTertiary = Background,

    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = PrimaryVariant,
    tertiary = Accent,

    background = White,
    surface = White,

    onPrimary = White,
    onSecondary = White,
    onTertiary = Background,

    onBackground = Background,
    onSurface = Background
)

@Composable
fun LifeResetOSTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
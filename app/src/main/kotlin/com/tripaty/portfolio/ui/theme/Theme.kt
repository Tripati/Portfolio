package com.tripaty.portfolio.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = AccentBlue,
    secondary = PurpleSecondary,
    tertiary = AccentGold,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color.White,
    onSurface = Color.White,
    onPrimary = Color.White,
)

private val LightColors = lightColorScheme(
    primary = AccentBlue,
    secondary = PurpleSecondary,
    tertiary = AccentGold,
    background = LightBackground,
    surface = LightSurface,
    onBackground = DarkNavy,
    onSurface = DarkNavy,
    onPrimary = Color.White,
)

@Composable
fun PortfolioTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}

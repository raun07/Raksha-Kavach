package com.rakshakavach.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = YellowPrimary,
    onPrimary = BlackBackground,
    background = BlackBackground,
    surface = DarkSurface,
    onSurface = Color.White,
    error = RiskCritical,
    onError = Color.White,
    tertiary = AccentBlue
)

@Composable
fun RakshaKavachTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
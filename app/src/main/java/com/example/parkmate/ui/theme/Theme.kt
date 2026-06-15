package com.example.parkmate.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ParkMateColorScheme = lightColorScheme(
    primary = Forest,
    onPrimary = Meadow,
    secondary = Trail,
    onSecondary = Meadow,
    background = Meadow,
    onBackground = Pine,
    surface = Meadow,
    onSurface = Pine,
    surfaceContainer = androidx.compose.ui.graphics.Color.White,
    onSurfaceVariant = Stone
)

@Composable
fun ParkMateTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ParkMateColorScheme,
        content = content
    )
}

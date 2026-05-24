package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = TacticalAmber,
    tertiary = CyberPurple,
    background = HudBlack,
    surface = HudDarkGrey,
    onPrimary = HudBlack,
    onSecondary = HudBlack,
    onTertiary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
    surfaceVariant = HudCardGrey,
    onSurfaceVariant = TextMuted,
    outline = HudBorderCyan
)

@Composable
fun ShonenStudyOSTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

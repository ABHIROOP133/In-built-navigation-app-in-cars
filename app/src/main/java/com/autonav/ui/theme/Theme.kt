package com.autonav.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AutomotiveDarkColorScheme = darkColorScheme(
    primary = SteelBlueAccent,
    onPrimary = TextPrimary,
    primaryContainer = SteelBlueMuted,
    onPrimaryContainer = TextPrimary,
    secondary = TextSecondary,
    onSecondary = CharcoalBackground,
    background = CharcoalBackground,
    onBackground = TextPrimary,
    surface = CharcoalSurface,
    onSurface = TextPrimary,
    surfaceVariant = CharcoalSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = StructuralBorder
)

/**
 * Enforces a consistent dark automotive color scheme regardless of device wallpaper or
 * OS light/dark toggle to maintain predictable cabin ergonomics.
 */
@Composable
fun AutoNavTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AutomotiveDarkColorScheme,
        typography = AutoNavTypography,
        content = content
    )
}

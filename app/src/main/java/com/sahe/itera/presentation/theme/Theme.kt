package com.sahe.itera.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    background       = BackgroundLight,
    surface          = SurfaceLight,
    surfaceVariant   = SurfaceSecondaryLight,
    onBackground     = OnBackgroundLight,
    onSurface        = OnBackgroundLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    primary          = AccentBlue,
    secondary        = AccentLila,
    tertiary         = AccentGreen,
    outline          = DividerLight
)

private val DarkColorScheme = darkColorScheme(
    background       = BackgroundDark,
    surface          = SurfaceDark,
    surfaceVariant   = CardDark,
    onBackground     = OnBackgroundDark,
    onSurface        = OnBackgroundDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    primary          = AccentBlue,
    secondary        = AccentLila,
    tertiary         = AccentGreen,
    outline          = DividerDark
)

@Composable
fun IteraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    val extendedColors = IteraExtendedColors(
        accentCoral  = AccentCoral,
        accentYellow = AccentYellow
    )

    CompositionLocalProvider(LocalIteraColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = IteraTypography,
            content     = content
        )
    }
}
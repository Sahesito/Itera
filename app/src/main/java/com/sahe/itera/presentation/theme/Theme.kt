package com.sahe.itera.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    background           = BackgroundLight,
    surface              = SurfaceLight,
    surfaceVariant       = SurfaceSecondaryLight,
    onBackground         = OnBackgroundLight,
    onSurface            = OnBackgroundLight,
    onSurfaceVariant     = OnSurfaceVariantLight,
    primary              = AccentBlue,
    secondary            = AccentLila,
    tertiary             = AccentGreen,
    outline              = DividerLight
)

private val DarkColorScheme = darkColorScheme(
    background           = BackgroundDark,
    surface              = SurfaceDark,
    surfaceVariant       = CardDark,
    onBackground         = OnBackgroundDark,
    onSurface            = OnBackgroundDark,
    onSurfaceVariant     = OnSurfaceVariantDark,
    primary              = AccentBlue,
    secondary            = AccentLila,
    tertiary             = AccentGreen,
    outline              = DividerDark
)

@Composable
fun IteraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val extendedColors = IteraExtendedColors(
        accentCoral   = AccentCoral,
        accentYellow  = AccentYellow
    )

    CompositionLocalProvider (LocalIteraColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = IteraTypography,
            content     = content
        )
    }
}
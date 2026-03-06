package com.sahe.itera.presentation.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class IteraExtendedColors(
    val accentCoral: Color,
    val accentYellow: Color
)

val LocalIteraColors = staticCompositionLocalOf {
    IteraExtendedColors(
        accentCoral   = Color.Unspecified,
        accentYellow  = Color.Unspecified
    )
}
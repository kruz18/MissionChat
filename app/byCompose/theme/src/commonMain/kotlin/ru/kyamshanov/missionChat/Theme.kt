package ru.kyamshanov.missionChat

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.ui.graphics.Color

object MissionTheme {
    val colors: ExtendedColorScheme
        @Composable
        get() = LocalExtendedColors.current
}

@Immutable
data class ExtendedColorScheme(
    val brand: ColorFamily,
    val success: ColorFamily,
    val warning: ColorFamily,
    val cosmicDeep: ColorFamily,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)

expect val LocalExtendedColors: ProvidableCompositionLocal<ExtendedColorScheme>

expect val AppShapes : Shapes


@Composable
expect fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,     // Dynamic color is available on Android 12+
    content: @Composable() () -> Unit
)
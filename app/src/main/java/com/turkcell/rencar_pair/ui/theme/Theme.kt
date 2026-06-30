package com.turkcell.rencar_pair.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary          = Blue500,
    onPrimary        = Color.White,
    background       = BackgroundLight,
    onBackground     = Neutral900,
    surface          = Color.White,
    onSurface        = Neutral900,
    onSurfaceVariant = Gray500,
    outline          = Gray300
)

private val DarkColorScheme = darkColorScheme(
    primary          = Blue400,
    onPrimary        = Color.White,
    background       = Dark300,
    onBackground     = Color(0xFFF9FAFB),
    surface          = Dark100,
    onSurface        = Color(0xFFF9FAFB),
    onSurfaceVariant = Gray400,
    outline          = InactiveIndicatorDark
)

@Composable
fun RenCarPairTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography  = RenCarTypography,
        content     = content
    )
}

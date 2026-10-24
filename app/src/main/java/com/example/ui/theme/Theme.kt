package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TropicalGreenDark,
    onPrimary = Color(0xFF003822),
    primaryContainer = TropicalGreenContainerDark,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = SkyBlueSecondaryDark,
    onSecondary = Color(0xFF00344F),
    secondaryContainer = SkyBlueContainerDark,
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = SunGoldTertiaryDark,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = SunGoldContainerDark,
    onTertiaryContainer = Color(0xFFFDE68A),
    background = DarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = TropicalGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = TropicalGreenContainer,
    onPrimaryContainer = Color(0xFF064E3B),
    secondary = SkyBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = SkyBlueContainer,
    onSecondaryContainer = Color(0xFF075985),
    tertiary = SunGoldTertiary,
    onTertiary = Color.White,
    tertiaryContainer = SunGoldContainer,
    onTertiaryContainer = Color(0xFF78350F),
    background = LightBackground,
    onBackground = Color(0xFF0F172A),
    surface = LightSurface,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF334155),
    outline = LightOutline
)

@Composable
fun CambioBrasilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our brand travel palette by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

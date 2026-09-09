package com.nomo.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = NomoWarmAmber,
    secondary = NomoSage,
    tertiary = NomoSage,
    background = NomoCream,
    surface = NomoSurface,
    onPrimary = NomoDeepCharcoal,
    onSecondary = NomoCream,
    onTertiary = NomoCream,
    onBackground = NomoDeepCharcoal,
    onSurface = NomoDeepCharcoal,
    surfaceVariant = NomoCream,
    onSurfaceVariant = NomoDeepCharcoal
)

@Composable
fun NOMOTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NomoCream.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

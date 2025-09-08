package com.andy.qrscannerandy.ui.theme

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
    primary = Color(0xFF00D18C),       // Verde brillante para contraste
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D3A),
    onPrimaryContainer = Color(0xFFB2F1DB),
    secondary = Color(0xFF66BB6A),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004D25),
    onSecondaryContainer = Color(0xFFDFF6E3),
    tertiary = Color(0xFF81C784),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF00391E),
    onTertiaryContainer = Color(0xFFC8E6C9),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00A86B),       // Verde principal
    onPrimary = Color.White,           // Texto sobre verde → blanco
    primaryContainer = Color(0xFFB2F1DB), // Fondo verde muy claro
    onPrimaryContainer = Color(0xFF002117),
    secondary = Color(0xFF4CAF50),     // Verde medio para acentos
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDFF6E3),
    onSecondaryContainer = Color(0xFF003921),
    tertiary = Color(0xFF388E3C),      // Verde oscuro para detalles
    onTertiary = Color.White,
    tertiaryContainer = Color(0xff44c749),
    onTertiaryContainer = Color(0xFF00210F),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1B1B1B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1B1B1B)
)

@Composable
fun QrCamTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
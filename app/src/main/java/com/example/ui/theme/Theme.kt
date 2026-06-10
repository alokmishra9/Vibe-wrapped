package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF1DB954),      // Spotify Green
    secondary = Color(0xFFBD00FF),    // Neon Purple
    tertiary = Color(0xFF00F0FF),     // Cyber Cyan
    background = Color(0xFF050505),
    surface = Color(0xFF111115),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0x14FFFFFF),
    outline = Color(0x1FFFFFFF)
  )

private val LightColorScheme = DarkColorScheme // Always use immersive dark mode for Wrapped style

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false, // Disable dynamic colors by default to preserve the brand's premium immersive look
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

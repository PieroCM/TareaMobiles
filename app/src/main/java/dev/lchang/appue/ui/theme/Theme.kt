package dev.lchang.appue.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = PlayGreen,
    onPrimary = Color.Black,
    secondary = PlayGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val LightColors = lightColorScheme()

@Composable
fun MySpotifyTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme || isSystemInDarkTheme()) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}


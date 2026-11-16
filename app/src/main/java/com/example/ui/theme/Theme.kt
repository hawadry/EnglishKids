package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = SunnyOrange,
    onPrimary = Color.White,
    primaryContainer = SunnyOrangeContainer,
    onPrimaryContainer = OnSunnyOrangeContainer,
    secondary = SkyTurquoise,
    onSecondary = Color.White,
    secondaryContainer = SkyTurquoiseContainer,
    onSecondaryContainer = OnSkyTurquoiseContainer,
    tertiary = BerryPurple,
    onTertiary = Color.White,
    tertiaryContainer = BerryPurpleContainer,
    onTertiaryContainer = OnBerryPurpleContainer,
    background = WarmCream,
    onBackground = DeepInk,
    surface = SoftPaper,
    onSurface = DeepInk,
    surfaceVariant = WarmSand,
    onSurfaceVariant = SlateInk,
    outline = OutlineSand
)

private val DarkColorScheme = darkColorScheme(
    primary = SunnyOrange,
    onPrimary = Color.White,
    primaryContainer = SunnyOrangeDark,
    onPrimaryContainer = Color.White,
    secondary = SkyTurquoise,
    onSecondary = Color.White,
    secondaryContainer = SkyTurquoiseDark,
    onSecondaryContainer = Color.White,
    tertiary = BerryPurple,
    onTertiary = Color.White,
    background = DarkCanvas,
    onBackground = DarkWarmText,
    surface = DarkSurfaceCard,
    onSurface = DarkWarmText,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = DarkMutedText
)

val KidsShapes = Shapes(
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun EnglishStarsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        typography = EnglishStarsTypography,
        shapes = KidsShapes,
        content = content
    )
}

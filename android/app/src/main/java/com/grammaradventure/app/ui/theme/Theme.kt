package com.grammaradventure.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = IndigoInk,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E6F7),
    onPrimaryContainer = IndigoDeep,
    secondary = EmeraldTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5F3EE),
    onSecondaryContainer = EmeraldDark,
    tertiary = SaffronWarm,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEDD5),
    onTertiaryContainer = Color(0xFF7C2D12),
    background = ParchmentBg,
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SoftSlateBg,
    onSurfaceVariant = TextSecondary,
    outline = OutlineMuted
)

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp)
)

@Composable
fun EnglishGrammarAdventureTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}

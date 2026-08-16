package com.notesup.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.color.utilities.Blend

private val LightScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

private val DarkScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)

private val DieciScheme = DarkScheme.copy(
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    surfaceContainerLowest = Color(0xFF000000),
)

private val InkScheme = DarkScheme.copy(
    background = Color(0xFF0C0A09),
    surface = Color(0xFF0C0A09),
    primary = Color(0xFFE7E5E4),
    onPrimary = Color(0xFF1C1917),
)

private val MidnightScheme = DarkScheme.copy(
    background = Color(0xFF0B1220),
    surface = Color(0xFF0B1220),
    primary = Color(0xFF93C5FD),
    primaryContainer = Color(0xFF1E3A5F),
)

private val SlateScheme = DarkScheme.copy(
    background = Color(0xFF1C1F24),
    surface = Color(0xFF1C1F24),
    primary = Color(0xFFA8B4C4),
    primaryContainer = Color(0xFF2A313C),
)

private val GraphiteScheme = LightScheme.copy(
    background = Color(0xFFE8E6E3),
    surface = Color(0xFFE8E6E3),
    primary = Color(0xFF3F3F46),
)

private val NoonScheme = LightScheme.copy(
    background = Color(0xFFFFFBF5),
    surface = Color(0xFFFFFBF5),
    primary = Color(0xFFB45309),
)

private val FogScheme = LightScheme.copy(
    background = Color(0xFFF1F4F7),
    surface = Color(0xFFF1F4F7),
    primary = Color(0xFF475569),
)

private val LegalScheme = LightScheme.copy(
    background = Color(0xFFF6EFD4),
    surface = Color(0xFFF6EFD4),
    primary = Color(0xFF5B4A1E),
)

private val KraftScheme = LightScheme.copy(
    background = Color(0xFFE8D5B5),
    surface = Color(0xFFE8D5B5),
    primary = Color(0xFF6B3F1D),
)

data class ThemeOption(val key: String, val labelRes: Int, val swatchSurface: Color, val swatchPrimary: Color)

val AppThemeOptions = listOf(
    ThemeOption("dynamic", com.notesup.app.R.string.theme_dynamic, Color(0xFFE8DEF8), Color(0xFF6750A4)),
    ThemeOption("dieci", com.notesup.app.R.string.theme_dieci, Color(0xFF000000), Color(0xFFFFB2C0)),
    ThemeOption("ink", com.notesup.app.R.string.theme_ink, Color(0xFF0C0A09), Color(0xFFE7E5E4)),
    ThemeOption("midnight", com.notesup.app.R.string.theme_midnight, Color(0xFF0B1220), Color(0xFF93C5FD)),
    ThemeOption("slate", com.notesup.app.R.string.theme_slate, Color(0xFF1C1F24), Color(0xFFA8B4C4)),
    ThemeOption("paper", com.notesup.app.R.string.theme_paper, Color(0xFFF6F1EA), Color(0xFF8B2942)),
    ThemeOption("graphite", com.notesup.app.R.string.theme_graphite, Color(0xFFE8E6E3), Color(0xFF3F3F46)),
    ThemeOption("noon", com.notesup.app.R.string.theme_noon, Color(0xFFFFFBF5), Color(0xFFB45309)),
    ThemeOption("fog", com.notesup.app.R.string.theme_fog, Color(0xFFF1F4F7), Color(0xFF475569)),
    ThemeOption("legal", com.notesup.app.R.string.theme_legal, Color(0xFFF6EFD4), Color(0xFF5B4A1E)),
    ThemeOption("kraft", com.notesup.app.R.string.theme_kraft, Color(0xFFE8D5B5), Color(0xFF6B3F1D)),
)

fun Color.harmonize(seed: Color): Color {
    val out = Blend.harmonize(this.toArgb(), seed.toArgb())
    return Color(out)
}

fun ColorScheme.harmonized(seed: Color = Seed): ColorScheme = copy(
    primary = primary.harmonize(seed),
    onPrimary = onPrimary.harmonize(seed),
    primaryContainer = primaryContainer.harmonize(seed),
    onPrimaryContainer = onPrimaryContainer.harmonize(seed),
    secondary = secondary.harmonize(seed),
    secondaryContainer = secondaryContainer.harmonize(seed),
    tertiary = tertiary.harmonize(seed),
    tertiaryContainer = tertiaryContainer.harmonize(seed),
    background = background.harmonize(seed),
    surface = surface.harmonize(seed),
    surfaceContainerLow = surfaceContainerLow.harmonize(seed),
    surfaceContainer = surfaceContainer.harmonize(seed),
    surfaceContainerHigh = surfaceContainerHigh.harmonize(seed),
    surfaceContainerHighest = surfaceContainerHighest.harmonize(seed),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NotesupTheme(
    themePref: String = "system",
    appTheme: String = "dynamic",
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val dark = when (themePref) {
        "light" -> false
        "dark" -> true
        else -> systemDark
    }
    val context = LocalContext.current
    val scheme = when (appTheme) {
        "dieci" -> DieciScheme
        "ink" -> InkScheme
        "midnight" -> MidnightScheme
        "slate" -> SlateScheme
        "paper" -> LightScheme
        "graphite" -> GraphiteScheme
        "noon" -> NoonScheme
        "fog" -> FogScheme
        "legal" -> LegalScheme
        "kraft" -> KraftScheme
        "dynamic" -> if (Build.VERSION.SDK_INT >= 31) {
            val dyn = if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            dyn.harmonized(Seed)
        } else {
            if (dark) DarkScheme else LightScheme
        }
        else -> if (dark) DarkScheme else LightScheme
    }

    MaterialExpressiveTheme(
        colorScheme = scheme,
        typography = notesupTypography(),
        shapes = NotesupShapes,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}

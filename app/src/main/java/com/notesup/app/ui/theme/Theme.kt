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
        "dynamic" -> if (Build.VERSION.SDK_INT >= 31) {
            val dyn = if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            dyn.harmonized(Seed)
        } else {
            if (dark) DarkScheme else LightScheme
        }
        "ink", "midnight", "slate" -> DarkScheme
        else -> if (dark && appTheme != "paper" && appTheme != "graphite" && appTheme != "noon" &&
            appTheme != "fog" && appTheme != "legal" && appTheme != "kraft"
        ) DarkScheme else LightScheme
    }

    MaterialExpressiveTheme(
        colorScheme = scheme,
        typography = notesupTypography(),
        shapes = NotesupShapes,
        motionScheme = MotionScheme.expressive(),
        content = content,
    )
}

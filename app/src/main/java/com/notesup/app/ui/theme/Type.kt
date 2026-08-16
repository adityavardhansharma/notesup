@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.notesup.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.notesup.app.R

fun flex(weight: Int, sizeSp: Float): FontFamily = FontFamily(
    Font(
        resId = R.font.roboto_flex_variable,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(
            FontVariation.weight(weight),
            FontVariation.Setting("opsz", sizeSp.coerceIn(8f, 144f)),
        ),
    ),
)

val Literata = FontFamily(Font(R.font.literata, FontWeight.Normal))
val JetBrainsMono = FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal))
val Atkinson = FontFamily(Font(R.font.atkinson_hyperlegible, FontWeight.Normal))

fun notesupTypography(): Typography {
    val body = flex(400, 16f)
    val title = flex(500, 16f)
    return Typography(
        displaySmall = TextStyle(
            fontFamily = flex(500, 36f),
            fontWeight = FontWeight.Medium,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = (-0.25).sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = flex(400, 28f),
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp,
            fontFeatureSettings = "liga",
        ),
        titleLarge = TextStyle(
            fontFamily = flex(500, 22f),
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = title,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = flex(500, 14f),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = flex(400, 14f),
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = flex(400, 12f),
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = flex(500, 14f),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = flex(500, 12f),
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = flex(500, 11f),
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp,
            fontFeatureSettings = "tnum",
        ),
    )
}

/** Maps a persisted note font key to a concrete family. */
fun noteFontFamily(font: String?): FontFamily = when (font) {
    "literata" -> Literata
    "jetbrains_mono", "mono" -> JetBrainsMono
    "atkinson" -> Atkinson
    else -> flex(400, 18f)
}

fun bodyNoteStyle(size: String, family: FontFamily = flex(400, 18f)) = TextStyle(
    fontFamily = family,
    fontWeight = FontWeight.Normal,
    fontSize = when (size) {
        "S" -> 16.sp
        "L" -> 20.sp
        else -> 18.sp
    },
    lineHeight = when (size) {
        "S" -> 25.sp
        "L" -> 31.sp
        else -> 28.sp
    },
    letterSpacing = 0.15.sp,
    fontFeatureSettings = "liga",
)

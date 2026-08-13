package com.notesup.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

@Composable
fun PaperGrain(
    modifier: Modifier = Modifier,
    intensity: Float = 0.025f,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        content()
        Canvas(Modifier.fillMaxSize()) {
            val step = 6f
            var y = 0f
            val rng = Random(7)
            while (y < size.height) {
                var x = 0f
                while (x < size.width) {
                    if (rng.nextFloat() < 0.18f) {
                        drawCircle(
                            color = Color.Black.copy(alpha = intensity),
                            radius = 0.7f,
                            center = androidx.compose.ui.geometry.Offset(x + rng.nextFloat() * 3f, y + rng.nextFloat() * 3f),
                        )
                    }
                    x += step
                }
                y += step
            }
        }
    }
}

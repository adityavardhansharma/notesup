package com.notesup.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.paperBackground(paper: String, color: Color): Modifier = drawBehind {
    val gap = 30.dp.toPx()
    val stroke = 1f
    when (paper) {
        "lines" -> {
            var y = gap
            while (y < size.height) {
                drawLine(color, Offset(0f, y), Offset(size.width, y), stroke)
                y += gap
            }
        }
        "grid" -> {
            var y = gap
            while (y < size.height) {
                drawLine(color, Offset(0f, y), Offset(size.width, y), stroke)
                y += gap
            }
            var x = gap
            while (x < size.width) {
                drawLine(color, Offset(x, 0f), Offset(x, size.height), stroke)
                x += gap
            }
        }
        "dots" -> {
            var y = gap
            while (y < size.height) {
                var x = gap
                while (x < size.width) {
                    drawCircle(color, 1.6f, Offset(x, y))
                    x += gap
                }
                y += gap
            }
        }
        else -> Unit
    }
}

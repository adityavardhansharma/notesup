package com.notesup.app.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InkCanvas(
    modifier: Modifier = Modifier,
    heightDp: Float = 280f,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.large),
    )
}

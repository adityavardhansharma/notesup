package com.notesup.app.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.notesup.app.R
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon

@Composable
fun Lightbox(model: Any, onClose: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.92f))) {
        AsyncImage(model, null, Modifier.fillMaxSize())
        IconButton(
            onClick = onClose,
            modifier = Modifier.statusBarsPadding().padding(8.dp).align(Alignment.TopStart),
        ) {
            NuIcon(NotesupIcons.Close, stringResource(R.string.cd_close_image), tint = Color.White)
        }
    }
}

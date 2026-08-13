package com.notesup.app.ui.media

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesup.app.R
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageSourceSheet(
    onDismiss: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(bottom = 24.dp)) {
            Text(stringResource(R.string.add_image), modifier = Modifier.padding(20.dp, 16.dp))
            ListItem(
                headlineContent = { Text(stringResource(R.string.take_photo)) },
                leadingContent = { NuIcon(NotesupIcons.Camera, stringResource(R.string.cd_camera)) },
                modifier = Modifier.fillMaxWidth().clickable(onClick = onCamera),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.choose_gallery)) },
                leadingContent = { NuIcon(NotesupIcons.Gallery, stringResource(R.string.cd_gallery)) },
                modifier = Modifier.fillMaxWidth().clickable(onClick = onGallery),
            )
        }
    }
}

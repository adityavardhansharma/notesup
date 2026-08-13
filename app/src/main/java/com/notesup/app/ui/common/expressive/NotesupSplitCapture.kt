package com.notesup.app.ui.common.expressive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesup.app.R
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.common.rememberHaptics

@Composable
fun NotesupSplitCapture(
    onCreate: (NoteKind) -> Unit,
    modifier: Modifier = Modifier,
) {
    var open by remember { mutableStateOf(false) }
    val haptics = rememberHaptics()
    Box(modifier) {
        Row {
            FilledIconButton(
                onClick = {
                    haptics.confirm()
                    onCreate(NoteKind.TEXT)
                },
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp, topEnd = 4.dp, bottomEnd = 4.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                NuIcon(NotesupIcons.Add, stringResource(R.string.cd_new))
            }
            FilledIconButton(
                onClick = {
                    haptics.tick()
                    open = true
                },
                modifier = Modifier
                    .height(56.dp)
                    .width(40.dp),
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, topStart = 4.dp, bottomStart = 4.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                NuIcon(NotesupIcons.Split, stringResource(R.string.cd_more_types))
            }
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            fun pick(kind: NoteKind) {
                haptics.tick()
                open = false
                onCreate(kind)
            }
            DropdownMenuItem(
                text = { Text(stringResource(R.string.type_text)) },
                onClick = { pick(NoteKind.TEXT) },
                leadingIcon = { NuIcon(NotesupIcons.Note, null) },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.type_list)) },
                onClick = { pick(NoteKind.CHECKLIST) },
                leadingIcon = { NuIcon(NotesupIcons.Checklist, null) },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.type_ink)) },
                onClick = { pick(NoteKind.INK) },
                leadingIcon = { NuIcon(NotesupIcons.Draw, null) },
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.type_image)) },
                onClick = { pick(NoteKind.IMAGE) },
                leadingIcon = { NuIcon(NotesupIcons.Image, null) },
            )
        }
    }
}

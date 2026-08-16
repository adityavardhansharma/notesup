package com.notesup.app.ui.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesup.app.R
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.domain.model.ProjectId
import com.notesup.app.ui.common.EmptySentence
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.common.expressive.NotesupSplitCapture
import com.notesup.app.ui.home.NoteCard

@Composable
fun ProjectScreen(
    projectId: String,
    onBack: () -> Unit,
    onOpenNote: (String, Boolean) -> Unit,
    vm: ProjectViewModel = hiltViewModel(),
) {
    androidx.compose.runtime.LaunchedEffect(projectId) { vm.attach(projectId) }
    val project by vm.project.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()
    val inbox = projectId == ProjectViewModel.INBOX
    var menu by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var edit by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var confirmDelete by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().height(64.dp).padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back)) }
            Text(
                if (inbox) stringResource(R.string.inbox) else (project?.name ?: ""),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
            if (!inbox) {
                IconButton(onClick = { menu = true }) { NuIcon(NotesupIcons.More, stringResource(R.string.cd_more)) }
                androidx.compose.material3.DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(stringResource(R.string.rename)) },
                        onClick = { menu = false; edit = true },
                    )
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(stringResource(R.string.delete)) },
                        onClick = { menu = false; confirmDelete = true },
                    )
                }
            }
        }
        if (notes.isEmpty()) {
            EmptySentence(stringResource(R.string.empty_project, if (inbox) stringResource(R.string.inbox) else (project?.name ?: "")))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(notes, key = { it.id.raw }) { n ->
                    NoteCard(n, onClick = { onOpenNote(n.id.raw, false) }, onLongClick = {})
                }
            }
        }
    }
    NotesupSplitCapture(
        onCreate = { kind -> vm.create(kind, if (inbox) null else ProjectId(projectId)) { onOpenNote(it, true) } },
        modifier = Modifier.align(androidx.compose.ui.Alignment.BottomEnd).navigationBarsPadding().padding(16.dp),
    )
    }
    if (edit && project != null) {
        ProjectEditSheet(
            onDismiss = { edit = false },
            onSave = { name, hue, emoji -> vm.update(name, hue, emoji); edit = false },
            initialName = project!!.name,
            initialHue = project!!.hue,
            initialEmoji = project!!.emoji,
            title = stringResource(R.string.edit_project),
        )
    }
    if (confirmDelete && project != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete_project, project!!.name)) },
            text = { Text(stringResource(R.string.delete_project_body)) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = { vm.delete(onBack); confirmDelete = false }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

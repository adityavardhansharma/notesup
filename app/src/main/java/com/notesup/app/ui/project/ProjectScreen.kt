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
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().height(64.dp).padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) { NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back)) }
            Text(
                project?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )
        }
        if (notes.isEmpty()) {
            EmptySentence(stringResource(R.string.empty_project, project?.name ?: ""))
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
        onCreate = { kind -> vm.create(kind, ProjectId(projectId)) { onOpenNote(it, true) } },
        modifier = Modifier.navigationBarsPadding().padding(16.dp),
    )
}

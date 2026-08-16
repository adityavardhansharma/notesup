package com.notesup.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.notesup.app.R
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.ui.common.EmptySentence
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.common.expressive.NotesupFilterPills
import com.notesup.app.ui.common.expressive.NotesupSplitCapture
import com.notesup.app.ui.common.rememberHaptics
import com.notesup.app.ui.project.ProjectEditSheet
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onOpenNote: (String, Boolean) -> Unit,
    onSearch: () -> Unit,
    onAccount: () -> Unit,
    onProject: (String) -> Unit,
    onSettings: () -> Unit,
    showRail: Boolean,
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val haptics = rememberHaptics()
    val snack = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var newProject by remember { mutableStateOf(false) }
    val selecting = state.selected.isNotEmpty()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            if (selecting) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = vm::clearSelect) { NuIcon(NotesupIcons.Close, stringResource(R.string.cd_close)) }
                    Text(stringResource(R.string.n_selected, state.selected.size), style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { haptics.confirm(); vm.pinSelected() }) { NuIcon(NotesupIcons.Pin, stringResource(R.string.cd_pin)) }
                    val deletedLabel = stringResource(R.string.deleted_n, state.selected.size)
                    val undoLabel = stringResource(R.string.undo)
                    IconButton(onClick = {
                        haptics.reject()
                        val ids = state.selected.toList()
                        vm.deleteSelected()
                        scope.launch {
                            val res = snack.showSnackbar(deletedLabel, actionLabel = undoLabel)
                            if (res == SnackbarResult.ActionPerformed) ids.forEach(vm::undoDelete)
                        }
                    }) { NuIcon(NotesupIcons.Delete, stringResource(R.string.menu_delete)) }
                }
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(start = 16.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Notesup", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    IconButton(onClick = onSearch) { NuIcon(NotesupIcons.Search, stringResource(R.string.cd_search)) }
                    IconButton(onClick = onAccount) {
                        if (state.avatar != null) {
                            AsyncImage(state.avatar, stringResource(R.string.cd_account), Modifier.size(32.dp).clip(CircleShape))
                        } else {
                            NuIcon(NotesupIcons.Account, stringResource(R.string.cd_account))
                        }
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    NotesupFilterPills(state.filter, vm::setFilter, Modifier.weight(1f))
                    if (state.filter != HomeFilter.Recent) {
                        IconButton(onClick = { vm.setView(HomeView.Grid) }) {
                            NuIcon(
                                NotesupIcons.Grid,
                                stringResource(R.string.cd_grid),
                                tint = if (state.view == HomeView.Grid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        IconButton(onClick = { vm.setView(HomeView.List) }) {
                            NuIcon(
                                NotesupIcons.List,
                                stringResource(R.string.cd_list),
                                tint = if (state.view == HomeView.List) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    if (state.filter == HomeFilter.Projects) {
                        IconButton(onClick = { newProject = true }) {
                            NuIcon(NotesupIcons.Add, stringResource(R.string.new_project))
                        }
                    }
                }
            }

            when {
                state.filter == HomeFilter.Projects -> ProjectList(state, onProject)
                state.notes.isEmpty() -> EmptySentence(
                    sentence = when (state.filter) {
                        HomeFilter.Pinned -> stringResource(R.string.empty_pinned)
                        HomeFilter.Recent -> stringResource(R.string.empty_recent)
                        else -> stringResource(R.string.empty_home)
                    },
                    wordmark = state.filter == HomeFilter.All,
                )
                state.view == HomeView.Grid && state.filter != HomeFilter.Recent -> {
                    if (state.filter == HomeFilter.All && state.pinned.isNotEmpty()) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(168.dp),
                        ) {
                            items(state.pinned.take(6), key = { it.id.raw }) { n ->
                                NoteCard(
                                    note = n,
                                    onClick = { onOpenNote(n.id.raw, false) },
                                    onLongClick = { haptics.longPress(); vm.toggleSelect(n.id.raw) },
                                    modifier = Modifier.width(148.dp),
                                )
                            }
                        }
                    }
                    val cols = 2
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(cols),
                        contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 96.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(state.notes, key = { it.id.raw }) { n ->
                            NoteCard(
                                note = n,
                                onClick = {
                                    if (selecting) vm.toggleSelect(n.id.raw) else onOpenNote(n.id.raw, false)
                                },
                                onLongClick = { haptics.longPress(); vm.toggleSelect(n.id.raw) },
                            )
                        }
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(bottom = 96.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.notes, key = { it.id.raw }) { n ->
                        NoteListRow(
                            note = n,
                            onClick = {
                                if (selecting) vm.toggleSelect(n.id.raw) else onOpenNote(n.id.raw, false)
                            },
                            onLongClick = { haptics.longPress(); vm.toggleSelect(n.id.raw) },
                        )
                    }
                }
            }
        }

        if (!selecting) {
            NotesupSplitCapture(
                onCreate = { kind ->
                    vm.create(kind) { id -> onOpenNote(id, true) }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }

        SnackbarHost(snack, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 72.dp))

        if (newProject) {
            ProjectEditSheet(
                onDismiss = { newProject = false },
                onSave = { name, hue, emoji ->
                    haptics.confirm()
                    vm.createProject(name, hue, emoji) { newProject = false }
                },
            )
        }
    }
}

@Composable
private fun ProjectList(state: HomeState, onProject: (String) -> Unit) {
    if (state.projects.isEmpty()) {
        EmptySentence(stringResource(R.string.empty_projects))
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 96.dp)) {
        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NuIcon(NotesupIcons.Inbox, null)
                Spacer(Modifier.width(12.dp))
                Text(stringResource(R.string.inbox), style = MaterialTheme.typography.titleMedium)
            }
        }
        items(state.projects, key = { it.id.raw }) { p ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FilledIconButton(
                    onClick = { onProject(p.id.raw) },
                    modifier = Modifier.size(10.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(),
                ) {}
                Spacer(Modifier.width(12.dp))
                Text((p.emoji ?: "") + p.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            }
        }
    }
}

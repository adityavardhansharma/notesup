package com.notesup.app.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.notesup.app.R
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.op.SearchQuery
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.home.NoteListRow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

@HiltViewModel
class SearchViewModel @Inject constructor(private val notes: NoteRepository) : ViewModel() {
    val query = MutableStateFlow("")

    @OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val results = query.debounce(40).mapLatest { q ->
        val parsed = SearchQuery.parse(q)
        val base = when {
            parsed.text.isBlank() && parsed.verb == null -> emptyList()
            // Verb-only queries (Pinned/Drawings/Images/Locked) have no text to
            // match, so start from every note and let the verb filter below narrow.
            parsed.text.isBlank() -> notes.allAlive()
            else -> notes.search(parsed.text)
        }
        base.filter { n ->
            when (parsed.verb) {
                com.notesup.app.domain.op.ParsedSearch.Verb.PIN -> n.pinned
                com.notesup.app.domain.op.ParsedSearch.Verb.INK -> n.blocks.any { it is com.notesup.app.domain.model.Block.Ink }
                com.notesup.app.domain.op.ParsedSearch.Verb.IMAGE -> n.blocks.any { it is com.notesup.app.domain.model.Block.Image }
                com.notesup.app.domain.op.ParsedSearch.Verb.LOCKED -> n.locked
                null -> true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList<Note>())
}

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    vm: SearchViewModel = hiltViewModel(),
) {
    val results by vm.results.collectAsStateWithLifecycle()
    var text by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it; vm.query.value = it },
                modifier = Modifier.weight(1f).height(56.dp).focusRequester(focus),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { NuIcon(NotesupIcons.Search, null) },
                trailingIcon = {
                    if (text.isNotEmpty()) IconButton(onClick = { text = ""; vm.query.value = "" }) {
                        NuIcon(NotesupIcons.Close, stringResource(R.string.cd_close))
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
            )
            IconButton(onClick = onBack) { NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back)) }
        }
        if (text.isEmpty()) {
            LazyRow(Modifier.padding(horizontal = 16.dp)) {
                item { FilterChip(false, { text = "Pinned"; vm.query.value = "pin" }, { Text(stringResource(R.string.verb_pinned)) }) }
                item { FilterChip(false, { text = "Drawings"; vm.query.value = "ink" }, { Text(stringResource(R.string.verb_drawings)) }, modifier = Modifier.padding(start = 8.dp)) }
                item { FilterChip(false, { text = "Images"; vm.query.value = "image" }, { Text(stringResource(R.string.verb_images)) }, modifier = Modifier.padding(start = 8.dp)) }
                item { FilterChip(false, { text = "Locked"; vm.query.value = "locked" }, { Text(stringResource(R.string.verb_locked)) }, modifier = Modifier.padding(start = 8.dp)) }
            }
        } else if (results.isEmpty()) {
            Text(stringResource(R.string.empty_search), modifier = Modifier.padding(16.dp, 48.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(results, key = { it.id.raw }) { n ->
                    NoteListRow(n, onClick = { onOpen(n.id.raw) }, onLongClick = {})
                }
            }
        }
    }
}

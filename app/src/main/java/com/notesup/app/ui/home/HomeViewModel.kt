package com.notesup.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.data.auth.AuthRepository
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.data.repo.ProjectRepository
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.domain.model.Project
import com.notesup.app.domain.model.ProjectId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeState(
    val notes: List<Note> = emptyList(),
    val pinned: List<Note> = emptyList(),
    val projects: List<Project> = emptyList(),
    val filter: HomeFilter = HomeFilter.All,
    val view: HomeView = HomeView.Grid,
    val selected: Set<String> = emptySet(),
    val signedIn: Boolean = false,
    val email: String? = null,
    val avatar: String? = null,
    val syncPaused: Boolean = false,
    val offline: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notes: NoteRepository,
    private val projects: ProjectRepository,
    private val prefs: NotesupPrefs,
    auth: AuthRepository,
) : ViewModel() {
    val filter = MutableStateFlow(HomeFilter.All)
    private val selected = MutableStateFlow<Set<String>>(emptySet())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val listed = filter.flatMapLatest { f ->
        when (f) {
            HomeFilter.All -> notes.observeHome()
            HomeFilter.Pinned -> notes.observePinned()
            HomeFilter.Recent -> notes.observeRecent()
            HomeFilter.Projects -> notes.observeHome()
        }
    }

    val state = combine(
        combine(listed, notes.observePinned(), projects.observe(), filter, prefs.homeView) { list, pins, projs, f, view ->
            Triple(Triple(list, pins, projs), f, view)
        },
        combine(selected, auth.user, prefs.syncPaused) { sel, user, paused -> Triple(sel, user, paused) },
    ) { a, b ->
        val (pack, f, view) = a
        val (list, pins, projs) = pack
        val (sel, user, paused) = b
        HomeState(
            notes = list,
            pinned = pins,
            projects = projs,
            filter = f,
            view = if (f == HomeFilter.Recent) HomeView.List else if (view == "list") HomeView.List else HomeView.Grid,
            selected = sel,
            signedIn = user != null,
            email = user?.email,
            avatar = user?.imageUrl,
            syncPaused = paused,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeState())

    fun setFilter(f: HomeFilter) {
        filter.value = f
    }

    fun setView(v: HomeView) {
        viewModelScope.launch { prefs.setHomeView(if (v == HomeView.List) "list" else "grid") }
    }

    fun create(kind: NoteKind, projectId: ProjectId? = null, onReady: (String) -> Unit) {
        viewModelScope.launch {
            val note = notes.create(kind, projectId)
            onReady(note.id.raw)
        }
    }

    fun toggleSelect(id: String) {
        selected.value = if (id in selected.value) selected.value - id else selected.value + id
    }

    fun clearSelect() {
        selected.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            selected.value.forEach { notes.setDeleted(com.notesup.app.domain.model.NoteId(it), true) }
            selected.value = emptySet()
        }
    }

    fun pinSelected() {
        viewModelScope.launch {
            selected.value.forEach { id ->
                notes.get(com.notesup.app.domain.model.NoteId(id))?.let { notes.save(it.copy(pinned = true)) }
            }
            selected.value = emptySet()
        }
    }

    fun undoDelete(id: String) {
        viewModelScope.launch { notes.setDeleted(com.notesup.app.domain.model.NoteId(id), false) }
    }

    fun createProject(name: String, hue: Int, emoji: String?, onDone: () -> Unit) {
        viewModelScope.launch {
            projects.create(name, hue, emoji)
            onDone()
        }
    }
}

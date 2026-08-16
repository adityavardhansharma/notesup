package com.notesup.app.ui.project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.data.repo.ProjectRepository
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.domain.model.ProjectId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val noteRepo: NoteRepository,
    private val projects: ProjectRepository,
) : ViewModel() {
    // Navigation3 doesn't seed SavedStateHandle from NavKey fields, so the id is
    // supplied explicitly by the screen (mirrors EditorViewModel.attach).
    private val idFlow = MutableStateFlow<ProjectId?>(null)

    fun attach(id: String) {
        if (idFlow.value == null) idFlow.value = ProjectId(id)
    }

    val project = idFlow.filterNotNull()
        .mapLatest { projects.get(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val notes = idFlow.filterNotNull()
        .flatMapLatest { noteRepo.observeProject(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun create(kind: NoteKind, projectId: ProjectId, onReady: (String) -> Unit) {
        viewModelScope.launch {
            val n = noteRepo.create(kind, projectId)
            onReady(n.id.raw)
        }
    }
}

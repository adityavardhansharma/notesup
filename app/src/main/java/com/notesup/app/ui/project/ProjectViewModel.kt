package com.notesup.app.ui.project

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.data.repo.ProjectRepository
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.domain.model.ProjectId
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val noteRepo: NoteRepository,
    private val projects: ProjectRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val id = ProjectId(
        savedStateHandle.get<String>("projectId")
            ?: savedStateHandle.get<String>("id")
            ?: "",
    )

    val project = flow { emit(projects.get(id)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val notes = noteRepo.observeProject(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun create(kind: NoteKind, projectId: ProjectId, onReady: (String) -> Unit) {
        viewModelScope.launch {
            val n = noteRepo.create(kind, projectId)
            onReady(n.id.raw)
        }
    }
}

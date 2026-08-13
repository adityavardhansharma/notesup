package com.notesup.app.ui.editor

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.BlockId
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.model.NoteId
import com.notesup.app.domain.model.RichText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EditorViewModel @Inject constructor(
    private val notes: NoteRepository,
    val prefs: NotesupPrefs,
) : ViewModel() {
    private val noteIdFlow = kotlinx.coroutines.flow.MutableStateFlow<NoteId?>(null)

    fun attach(id: String) {
        noteIdFlow.value = NoteId(id)
    }

    private fun requireId(): NoteId = noteIdFlow.value ?: NoteId("")

    val note = noteIdFlow.filterNotNull().flatMapLatest { notes.observe(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val focusedBlock = MutableStateFlow<String?>(null)
    val titleState = TextFieldState()
    private val fields = mutableMapOf<String, TextFieldState>()

    fun fieldFor(block: Block.Paragraph): TextFieldState =
        fields.getOrPut(block.id.raw) { TextFieldState(block.rich.text) }

    fun fieldForHeading(block: Block.Heading): TextFieldState =
        fields.getOrPut(block.id.raw) { TextFieldState(block.text) }

    fun bind(n: Note) {
        if (titleState.text.toString() != n.title && !titleState.text.isNotEmpty()) {
            titleState.edit { replace(0, length, n.title) }
        }
    }

    @OptIn(FlowPreview::class)
    fun autosave() {
        viewModelScope.launch {
            val current = notes.get(requireId()) ?: return@launch
            val title = titleState.text.toString()
            val blocks = current.blocks.map { b ->
                when (b) {
                    is Block.Paragraph -> {
                        val t = fields[b.id.raw]?.text?.toString() ?: b.rich.text
                        b.copy(rich = RichText.of(t))
                    }
                    is Block.Heading -> {
                        val t = fields[b.id.raw]?.text?.toString() ?: b.text
                        b.copy(text = t)
                    }
                    else -> b
                }
            }
            notes.save(current.copy(title = title, blocks = blocks))
        }
    }

    fun setBlocks(transform: (List<Block>) -> List<Block>) {
        viewModelScope.launch {
            val current = notes.get(requireId()) ?: return@launch
            notes.save(current.copy(blocks = transform(current.blocks)))
        }
    }

    fun insert(block: Block, after: BlockId? = null) {
        setBlocks { list ->
            if (after == null) list + block
            else {
                val i = list.indexOfFirst { it.id == after }
                if (i < 0) list + block else list.toMutableList().also { it.add(i + 1, block) }
            }
        }
    }

    fun togglePin() = viewModelScope.launch {
        notes.get(requireId())?.let { notes.save(it.copy(pinned = !it.pinned)) }
    }

    fun setTint(tint: Int) = viewModelScope.launch {
        notes.get(requireId())?.let { notes.save(it.copy(tint = tint)) }
    }

    fun setPaper(paper: String) = viewModelScope.launch {
        notes.get(requireId())?.let { notes.save(it.copy(paper = paper)) }
    }

    fun setFont(font: String?) = viewModelScope.launch {
        notes.get(requireId())?.let { notes.save(it.copy(font = font)) }
    }

    fun delete() = viewModelScope.launch {
        notes.setDeleted(requireId(), true)
    }

    fun setLocked(locked: Boolean, cipher: ByteArray? = null) = viewModelScope.launch {
        notes.get(requireId())?.let { notes.save(it.copy(locked = locked, lockCipher = cipher)) }
    }

    suspend fun current(): Note? = notes.get(requireId())

    fun hydrateTitle(n: Note) {
        if (titleState.text.isEmpty() && n.title.isNotEmpty()) {
            titleState.edit { replace(0, length, n.title) }
        }
    }

    init {
        viewModelScope.launch {
            noteIdFlow.filterNotNull().first().let { id ->
                notes.observe(id).filterNotNull().first().let { hydrateTitle(it) }
            }
        }
    }
}

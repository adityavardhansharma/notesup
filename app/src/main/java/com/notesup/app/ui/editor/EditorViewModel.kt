package com.notesup.app.ui.editor

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.repo.MediaRepository
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.BlockId
import com.notesup.app.domain.model.CheckItem
import com.notesup.app.domain.model.MediaId
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.model.NoteId
import com.notesup.app.domain.model.RichText
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
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
    private val mediaRepo: MediaRepository,
    val prefs: NotesupPrefs,
    @com.notesup.app.di.ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {
    private val noteIdFlow = MutableStateFlow<NoteId?>(null)

    fun attach(id: String) {
        if (noteIdFlow.value == null) noteIdFlow.value = NoteId(id)
    }

    private fun requireId(): NoteId = noteIdFlow.value ?: NoteId("")

    val note = noteIdFlow.filterNotNull().flatMapLatest { notes.observe(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val focusedBlock = MutableStateFlow<String?>(null)
    val titleState = TextFieldState()

    // One editable text buffer per editable slot. Keys are namespaced so a block id
    // and one of its list items can never collide.
    private val fields = mutableMapOf<String, TextFieldState>()

    fun fieldFor(block: Block.Paragraph): TextFieldState =
        fields.getOrPut("p:${block.id.raw}") { TextFieldState(block.rich.text) }

    fun fieldForHeading(block: Block.Heading): TextFieldState =
        fields.getOrPut("h:${block.id.raw}") { TextFieldState(block.text) }

    fun fieldForCheck(item: CheckItem): TextFieldState =
        fields.getOrPut("c:${item.id}") { TextFieldState(item.text) }

    fun fieldForBullet(block: Block.Bullets, index: Int): TextFieldState =
        fields.getOrPut("b:${block.id.raw}:$index") { TextFieldState(block.items.getOrElse(index) { "" }) }

    fun fieldForNumber(block: Block.Numbered, index: Int): TextFieldState =
        fields.getOrPut("n:${block.id.raw}:$index") { TextFieldState(block.items.getOrElse(index) { "" }) }

    fun fieldForQuote(block: Block.Quote): TextFieldState =
        fields.getOrPut("q:${block.id.raw}") { TextFieldState(block.text) }

    fun fieldForCode(block: Block.Code): TextFieldState =
        fields.getOrPut("d:${block.id.raw}") { TextFieldState(block.text) }

    private fun snapshot(): Map<String, String> = fields.mapValues { it.value.text.toString() }

    /** Fold the live text buffers back into the block list. */
    private fun fold(snap: Map<String, String>, blocks: List<Block>): List<Block> = blocks.map { b ->
        when (b) {
            is Block.Paragraph -> b.copy(rich = RichText.of(snap["p:${b.id.raw}"] ?: b.rich.text))
            is Block.Heading -> b.copy(text = snap["h:${b.id.raw}"] ?: b.text)
            is Block.Checklist -> b.copy(items = b.items.map { it.copy(text = snap["c:${it.id}"] ?: it.text) })
            is Block.Bullets -> b.copy(items = b.items.mapIndexed { i, t -> snap["b:${b.id.raw}:$i"] ?: t })
            is Block.Numbered -> b.copy(items = b.items.mapIndexed { i, t -> snap["n:${b.id.raw}:$i"] ?: t })
            is Block.Quote -> b.copy(text = snap["q:${b.id.raw}"] ?: b.text)
            is Block.Code -> b.copy(text = snap["d:${b.id.raw}"] ?: b.text)
            else -> b
        }
    }

    /**
     * Persist the note on the application scope, first folding every pending text
     * edit so structural changes (toggling a checkbox, inserting a block, changing
     * color) never drop what the user has typed. Runs off viewModelScope so a save
     * triggered right before back navigation still completes.
     */
    private fun commit(transform: (Note) -> Note = { it }) {
        val id = requireId()
        val title = titleState.text.toString()
        val snap = snapshot()
        appScope.launch {
            val current = notes.get(id) ?: return@launch
            val folded = current.copy(title = title, blocks = fold(snap, current.blocks))
            notes.save(transform(folded))
        }
    }

    fun autosave() = commit()

    fun insert(block: Block, after: BlockId? = null) = commit { n ->
        val list = n.blocks
        val next = if (after == null) {
            list + block
        } else {
            val i = list.indexOfFirst { it.id == after }
            if (i < 0) list + block else list.toMutableList().also { it.add(i + 1, block) }
        }
        n.copy(blocks = next)
    }

    fun toggleCheck(blockId: String, itemId: String) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                if (b is Block.Checklist && b.id.raw == blockId) {
                    b.copy(items = b.items.map { if (it.id == itemId) it.copy(checked = !it.checked) else it })
                } else {
                    b
                }
            },
        )
    }

    fun addCheckItem(blockId: String) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                if (b is Block.Checklist && b.id.raw == blockId) {
                    b.copy(items = b.items + CheckItem(UUID.randomUUID().toString(), "", false))
                } else {
                    b
                }
            },
        )
    }

    fun addListItem(blockId: String) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                when {
                    b is Block.Bullets && b.id.raw == blockId -> b.copy(items = b.items + "")
                    b is Block.Numbered && b.id.raw == blockId -> b.copy(items = b.items + "")
                    else -> b
                }
            },
        )
    }

    fun insertImage(uri: Uri) {
        val id = requireId()
        val title = titleState.text.toString()
        val snap = snapshot()
        appScope.launch {
            val entity = mediaRepo.import(id, uri) ?: return@launch
            val current = notes.get(id) ?: return@launch
            val block = Block.Image(BlockId.random(), MediaId(entity.id), "")
            val folded = fold(snap, current.blocks)
            notes.save(current.copy(title = title, blocks = folded + block))
        }
    }

    fun togglePin() = commit { it.copy(pinned = !it.pinned) }
    fun setTint(tint: Int) = commit { it.copy(tint = tint) }
    fun setPaper(paper: String) = commit { it.copy(paper = paper) }
    fun setFont(font: String?) = commit { it.copy(font = font) }

    fun setLocked(locked: Boolean) = commit {
        it.copy(locked = locked, lockCipher = if (locked) it.lockCipher else null)
    }

    fun delete() {
        val id = requireId()
        appScope.launch { notes.setDeleted(id, true) }
    }

    suspend fun current(): Note? = notes.get(requireId())

    /** Fold pending edits, persist, and return the up-to-date note (for export/share). */
    suspend fun persistNow(): Note? {
        val id = requireId()
        val title = titleState.text.toString()
        val current = notes.get(id) ?: return null
        val folded = current.copy(title = title, blocks = fold(snapshot(), current.blocks))
        notes.save(folded)
        return folded
    }

    private fun hydrateTitle(n: Note) {
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

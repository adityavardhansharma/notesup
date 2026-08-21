package com.notesup.app.ui.editor

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.R
import com.notesup.app.data.crypto.LockUnavailableException
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.repo.MediaRepository
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.BlockId
import com.notesup.app.domain.model.CheckItem
import com.notesup.app.domain.model.MediaId
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.model.NoteId
import com.notesup.app.domain.model.ProjectId
import com.notesup.app.domain.model.RichSpan
import com.notesup.app.domain.model.RichText
import com.notesup.app.domain.model.SpanStyleTag
import com.notesup.app.domain.model.remapSpans
import com.notesup.app.domain.model.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val projects: com.notesup.app.data.repo.ProjectRepository,
    val inkRepo: com.notesup.app.data.repo.InkRepository,
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
    val focusedField = MutableStateFlow<String?>(null)
    val styleEpoch = MutableStateFlow(0)
    val titleState = TextFieldState()
    val projectsFlow = projects.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // One editable text buffer per editable slot. Keys are namespaced so a block id
    // and one of its list items can never collide.
    private val fields = mutableMapOf<String, TextFieldState>()
    private val spans = mutableMapOf<String, List<RichSpan>>()
    private val lastText = mutableMapOf<String, String>()

    fun fieldFor(block: Block.Paragraph): TextFieldState {
        val key = "p:${block.id.raw}"
        spans.getOrPut(key) { block.rich.spans }
        lastText.getOrPut(key) { block.rich.text }
        return fields.getOrPut(key) { TextFieldState(block.rich.text) }
    }

    fun spansFor(key: String): List<RichSpan> = spans[key].orEmpty()

    fun focusField(key: String?) {
        focusedField.value = key
        focusedBlock.value = key?.substringAfter(':')?.substringBefore(':')
    }

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

    fun fieldForTable(block: Block.Table, index: Int): TextFieldState =
        fields.getOrPut("t:${block.id.raw}:$index") { TextFieldState(block.cells.getOrElse(index) { "" }) }

    fun fieldForCaption(block: Block.Image): TextFieldState =
        fields.getOrPut("img:${block.id.raw}") { TextFieldState(block.caption) }

    private fun snapshot(): Map<String, String> = fields.mapValues { it.value.text.toString() }

    /** Fold the live text buffers back into the block list. */
    private fun fold(snap: Map<String, String>, blocks: List<Block>): List<Block> = blocks.map { b ->
        when (b) {
            is Block.Paragraph -> {
                val key = "p:${b.id.raw}"
                val text = snap[key] ?: b.rich.text
                val remapped = remapSpans(lastText[key] ?: b.rich.text, text, spans[key] ?: b.rich.spans)
                spans[key] = remapped
                lastText[key] = text
                b.copy(rich = RichText(text = text, spans = remapped))
            }
            is Block.Heading -> b.copy(text = snap["h:${b.id.raw}"] ?: b.text)
            is Block.Checklist -> b.copy(items = b.items.map { it.copy(text = snap["c:${it.id}"] ?: it.text) })
            is Block.Bullets -> b.copy(items = b.items.mapIndexed { i, t -> snap["b:${b.id.raw}:$i"] ?: t })
            is Block.Numbered -> b.copy(items = b.items.mapIndexed { i, t -> snap["n:${b.id.raw}:$i"] ?: t })
            is Block.Quote -> b.copy(text = snap["q:${b.id.raw}"] ?: b.text)
            is Block.Code -> b.copy(text = snap["d:${b.id.raw}"] ?: b.text)
            is Block.Table -> {
                val cells = List(b.rows * b.cols) { i -> snap["t:${b.id.raw}:$i"] ?: b.cells.getOrElse(i) { "" } }
                b.copy(cells = cells)
            }
            is Block.Image -> b.copy(caption = snap["img:${b.id.raw}"] ?: b.caption)
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
            // A save must never take the process down: locking depends on Keystore,
            // which fails on a device with no screen lock.
            runCatching { notes.save(transform(folded)) }.onFailure { err ->
                _notice.tryEmit(
                    if (err is LockUnavailableException) R.string.lock_needs_screen_lock else R.string.save_failed,
                )
            }
        }
    }

    private val _notice = MutableSharedFlow<Int>(extraBufferCapacity = 1)

    /** One-shot user-facing messages, as string resource ids. */
    val notice: SharedFlow<Int> = _notice

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

    /** True when this device can hold a note-lock key (it needs a screen lock). */
    fun canLock(): Boolean = notes.canLock()

    fun setLocked(locked: Boolean) {
        if (locked && !notes.canLock()) {
            _notice.tryEmit(R.string.lock_needs_screen_lock)
            return
        }
        commit { note ->
            if (locked) {
                unlockedCacheKeep(note)
                note.copy(locked = true)
            } else {
                note.copy(locked = false, lockCipher = null)
            }
        }
    }

    fun unlock() {
        viewModelScope.launch { notes.unlockSession(requireId()) }
    }

    fun moveToProject(projectId: String?) = commit {
        it.copy(projectId = projectId?.let(::ProjectId))
    }

    fun toggleStyle(style: SpanStyleTag, href: String? = null) {
        val key = focusedField.value ?: return
        if (!key.startsWith("p:")) return
        val field = fields[key] ?: return
        val sel = field.selection
        val start = minOf(sel.start, sel.end)
        val end = maxOf(sel.start, sel.end)
        if (start == end) return
        val text = field.text.toString()
        val remapped = remapSpans(lastText[key] ?: text, text, spans[key].orEmpty())
        spans[key] = remapped.toggle(start, end, style, href)
        lastText[key] = text
        styleEpoch.value++
        commit()
    }

    fun activeStyles(): Set<SpanStyleTag> {
        val key = focusedField.value ?: return emptySet()
        val field = fields[key] ?: return emptySet()
        val sel = field.selection
        val start = minOf(sel.start, sel.end)
        val end = maxOf(sel.start, sel.end)
        if (start == end) return emptySet()
        return spans[key].orEmpty().filter { it.start < end && it.end > start }.map { it.style }.toSet()
    }

    fun linkAtCaret(): String? {
        val key = focusedField.value ?: return null
        val field = fields[key] ?: return null
        val caret = field.selection.start
        return spans[key].orEmpty().firstOrNull { it.style == SpanStyleTag.LINK && caret in it.start until it.end }?.href
    }

    fun addCheckItemAfter(blockId: String, afterId: String) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                if (b is Block.Checklist && b.id.raw == blockId) {
                    val i = b.items.indexOfFirst { it.id == afterId }
                    val next = b.items.toMutableList()
                    next.add(if (i < 0) next.size else i + 1, CheckItem(UUID.randomUUID().toString(), "", false))
                    b.copy(items = next)
                } else {
                    b
                }
            },
        )
    }

    fun removeCheckItem(blockId: String, itemId: String) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                if (b is Block.Checklist && b.id.raw == blockId) {
                    val next = b.items.filter { it.id != itemId }
                    b.copy(items = next.ifEmpty { listOf(CheckItem(UUID.randomUUID().toString(), "", false)) })
                } else {
                    b
                }
            },
        )
    }

    fun reorderCheck(blockId: String, from: Int, to: Int) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                if (b is Block.Checklist && b.id.raw == blockId) {
                    val next = b.items.toMutableList()
                    if (from in next.indices && to in next.indices) {
                        val item = next.removeAt(from)
                        next.add(to, item)
                    }
                    b.copy(items = next)
                } else {
                    b
                }
            },
        )
    }

    fun addListItemAfter(blockId: String, index: Int) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                when {
                    b is Block.Bullets && b.id.raw == blockId -> {
                        val next = b.items.toMutableList()
                        next.add((index + 1).coerceIn(0, next.size), "")
                        b.copy(items = next)
                    }
                    b is Block.Numbered && b.id.raw == blockId -> {
                        val next = b.items.toMutableList()
                        next.add((index + 1).coerceIn(0, next.size), "")
                        b.copy(items = next)
                    }
                    else -> b
                }
            },
        )
    }

    fun removeListItem(blockId: String, index: Int) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                when {
                    b is Block.Bullets && b.id.raw == blockId -> {
                        val next = b.items.toMutableList()
                        if (index in next.indices) next.removeAt(index)
                        b.copy(items = next.ifEmpty { listOf("") })
                    }
                    b is Block.Numbered && b.id.raw == blockId -> {
                        val next = b.items.toMutableList()
                        if (index in next.indices) next.removeAt(index)
                        b.copy(items = next.ifEmpty { listOf("") })
                    }
                    else -> b
                }
            },
        )
    }

    fun addTableRow(blockId: String) = mutateTable(blockId) { t ->
        t.copy(rows = t.rows + 1, cells = t.cells + List(t.cols) { "" })
    }

    fun addTableCol(blockId: String) = mutateTable(blockId) { t ->
        val cells = MutableList((t.cols + 1) * t.rows) { "" }
        for (r in 0 until t.rows) {
            for (c in 0 until t.cols) {
                cells[r * (t.cols + 1) + c] = t.cells.getOrElse(r * t.cols + c) { "" }
            }
        }
        t.copy(cols = t.cols + 1, cells = cells)
    }

    fun removeTableRow(blockId: String) = mutateTable(blockId) { t ->
        if (t.rows <= 1) t else t.copy(rows = t.rows - 1, cells = t.cells.take((t.rows - 1) * t.cols))
    }

    fun removeTableCol(blockId: String) = mutateTable(blockId) { t ->
        if (t.cols <= 1) t else {
            val cols = t.cols - 1
            val cells = MutableList(cols * t.rows) { "" }
            for (r in 0 until t.rows) {
                for (c in 0 until cols) {
                    cells[r * cols + c] = t.cells.getOrElse(r * t.cols + c) { "" }
                }
            }
            t.copy(cols = cols, cells = cells)
        }
    }

    fun toggleTableHeader(blockId: String) = mutateTable(blockId) { it.copy(headerRow = !it.headerRow) }

    private fun mutateTable(blockId: String, transform: (Block.Table) -> Block.Table) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                if (b is Block.Table && b.id.raw == blockId) transform(b) else b
            },
        )
    }

    fun removeBlock(blockId: String) = commit { n ->
        n.copy(blocks = n.blocks.filter { it.id.raw != blockId })
    }

    fun replaceImage(blockId: String, uri: android.net.Uri) {
        val id = requireId()
        val title = titleState.text.toString()
        val snap = snapshot()
        appScope.launch {
            val entity = mediaRepo.import(id, uri) ?: return@launch
            val current = notes.get(id) ?: return@launch
            val folded = fold(snap, current.blocks).map { b ->
                if (b is Block.Image && b.id.raw == blockId) b.copy(mediaId = MediaId(entity.id)) else b
            }
            notes.save(current.copy(title = title, blocks = folded))
        }
    }

    fun updateInk(blockId: String, inkId: String, previewPath: String?) = commit { n ->
        n.copy(
            blocks = n.blocks.map { b ->
                if (b is Block.Ink && b.id.raw == blockId) b.copy(inkId = com.notesup.app.domain.model.InkId(inkId), previewPath = previewPath) else b
            },
        )
    }

    private fun unlockedCacheKeep(note: Note) {
        // session cache is maintained by NoteRepository.save while locked
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

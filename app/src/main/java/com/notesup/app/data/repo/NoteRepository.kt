package com.notesup.app.data.repo

import com.notesup.app.data.local.NoteDao
import com.notesup.app.data.local.entities.SyncQueueEntity
import com.notesup.app.data.local.toDomain
import com.notesup.app.data.local.toEntity
import com.notesup.app.data.local.toFts
import com.notesup.app.data.local.SyncQueueDao
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.model.NoteId
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.domain.model.ProjectId
import com.notesup.app.domain.model.RichText
import com.notesup.app.domain.op.NoteOps
import com.notesup.app.widget.WidgetUpdater
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class NoteRepository @Inject constructor(
    private val notes: NoteDao,
    private val queue: SyncQueueDao,
    private val prefs: NotesupPrefs,
    private val widgets: WidgetUpdater,
) {
    fun observeHome(): Flow<List<Note>> = notes.observeAlive().map { list -> list.map { it.toDomain() } }
    fun observePinned(): Flow<List<Note>> = notes.observePinned().map { list -> list.map { it.toDomain() } }
    fun observeRecent(): Flow<List<Note>> = notes.observeRecent().map { list -> list.map { it.toDomain() } }
    fun observeTrash(): Flow<List<Note>> = notes.observeTrash().map { list -> list.map { it.toDomain() } }
    fun observeProject(id: ProjectId?): Flow<List<Note>> =
        notes.observeInProject(id?.raw).map { list -> list.map { it.toDomain() } }

    fun observe(id: NoteId): Flow<Note?> = notes.observe(id.raw).map { it?.toDomain() }

    suspend fun get(id: NoteId): Note? = notes.get(id.raw)?.toDomain()

    suspend fun countAlive(): Int = notes.countAlive()

    suspend fun create(
        kind: NoteKind,
        projectId: ProjectId? = null,
        extraBlocks: List<Block> = emptyList(),
        title: String = "",
    ): Note {
        val now = Instant.now()
        val install = prefs.installId()
        val paper = prefs.defaultPaper.first()
        val font = prefs.defaultFont.first()
        val note = Note(
            id = NoteId.random(),
            remoteId = null,
            projectId = projectId,
            title = title,
            blocks = extraBlocks.ifEmpty { NoteOps.starterBlocks(kind) },
            pinned = false,
            locked = false,
            lockCipher = null,
            tint = 0,
            paper = paper,
            font = font,
            createdAt = now,
            updatedAt = now,
            deletedAt = null,
            rev = 1,
            writerId = install,
            baseRev = 0,
            baseWriterId = null,
        )
        save(note)
        return note
    }

    suspend fun save(note: Note) {
        val install = prefs.installId()
        val bumped = note.copy(
            updatedAt = Instant.now(),
            rev = note.rev + 1,
            writerId = install,
        )
        notes.upsertWithFts(bumped.toEntity(), bumped.toFts())
        queue.enqueueLatest(
            SyncQueueEntity(
                kind = "note",
                localId = bumped.id.raw,
                payloadJson = "",
                createdAt = System.currentTimeMillis(),
                attempts = 0,
            ),
        )
        widgets.schedule()
    }

    suspend fun setDeleted(id: NoteId, deleted: Boolean) {
        val current = notes.get(id.raw) ?: return
        val domain = current.toDomain()
        val next = domain.copy(
            deletedAt = if (deleted) Instant.now() else null,
        )
        save(next)
    }

    suspend fun search(match: String): List<Note> {
        val q = match.trim().ifBlank { return emptyList() }
        val fts = q.split(Regex("\\s+")).joinToString(" ") { token ->
            val t = token.replace("\"", "")
            if (t.endsWith("*")) t else "$t*"
        }
        return runCatching { notes.search(fts).map { it.toDomain() } }.getOrDefault(emptyList())
    }

    suspend fun purgeTrash() {
        val cutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        notes.purgeTrash(cutoff)
    }

    suspend fun emptyTrash() {
        notes.emptyTrash()
        widgets.schedule()
    }

    suspend fun applyPlainBody(id: NoteId, text: String) {
        val note = get(id) ?: return
        val blocks = if (note.blocks.isEmpty()) {
            listOf(Block.Paragraph(com.notesup.app.domain.model.BlockId.random(), RichText.of(text)))
        } else {
            note.blocks.mapIndexed { i, b ->
                if (i == 0 && b is Block.Paragraph) b.copy(rich = RichText.of(text)) else b
            }
        }
        save(note.copy(blocks = blocks))
    }
}

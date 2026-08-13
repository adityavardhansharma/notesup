package com.notesup.app.domain.model

import java.time.Instant

data class Note(
    val id: NoteId,
    val remoteId: String?,
    val projectId: ProjectId?,
    val title: String,
    val blocks: List<Block>,
    val pinned: Boolean,
    val locked: Boolean,
    val lockCipher: ByteArray?,
    val tint: Int,
    val paper: String,
    val font: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?,
    val rev: Long,
    val writerId: String,
    val baseRev: Long,
    val baseWriterId: String?,
) {
    val displayTitle: String
        get() = title.ifBlank {
            if (locked) "Locked note"
            else blocks.firstNotNullOfOrNull { b ->
                val t = b.plain().trim()
                t.takeIf { it.isNotEmpty() }?.take(48)
            } ?: "Untitled"
        }

    val preview: String
        get() = if (locked) "Locked note" else {
            blocks.asSequence()
                .map { it.plain().trim() }
                .firstOrNull { it.isNotEmpty() && it != title }
                ?: blocks.firstOrNull()?.plain()?.trim().orEmpty()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Note) return false
        return id == other.id && rev == other.rev && updatedAt == other.updatedAt
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class NoteKind { TEXT, CHECKLIST, INK, IMAGE }

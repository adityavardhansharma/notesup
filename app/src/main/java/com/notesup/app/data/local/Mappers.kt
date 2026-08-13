package com.notesup.app.data.local

import com.notesup.app.data.local.entities.NoteEntity
import com.notesup.app.data.local.entities.NoteFts
import com.notesup.app.data.local.entities.ProjectEntity
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.model.NoteId
import com.notesup.app.domain.model.Project
import com.notesup.app.domain.model.ProjectId
import com.notesup.app.domain.op.NoteOps
import java.time.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Jsons {
    val blocks: Json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "kind"
        encodeDefaults = true
    }
}

fun NoteEntity.toDomain(): Note = Note(
    id = NoteId(id),
    remoteId = remoteId,
    projectId = projectId?.let(::ProjectId),
    title = title,
    blocks = runCatching { Jsons.blocks.decodeFromString<List<Block>>(blocksJson) }.getOrDefault(emptyList()),
    pinned = pinned,
    locked = locked,
    lockCipher = lockCipher,
    tint = tint,
    paper = paper,
    font = font,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    deletedAt = deletedAt?.let(Instant::ofEpochMilli),
    rev = rev,
    writerId = writerId,
    baseRev = baseRev,
    baseWriterId = baseWriterId,
)

fun Note.toEntity(): NoteEntity = NoteEntity(
    id = id.raw,
    remoteId = remoteId,
    projectId = projectId?.raw,
    title = title,
    blocksJson = Jsons.blocks.encodeToString(blocks),
    plaintext = if (locked) "" else NoteOps.materializePlaintext(title, blocks),
    pinned = pinned,
    locked = locked,
    lockCipher = lockCipher,
    tint = tint,
    paper = paper,
    font = font,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    deletedAt = deletedAt?.toEpochMilli(),
    rev = rev,
    writerId = writerId,
    baseRev = baseRev,
    baseWriterId = baseWriterId,
)

fun Note.toFts(): NoteFts? =
    if (locked) null
    else NoteFts(noteId = id.raw, plaintext = NoteOps.materializePlaintext(title, blocks))

fun ProjectEntity.toDomain(): Project = Project(
    id = ProjectId(id),
    remoteId = remoteId,
    name = name,
    hue = hue,
    emoji = emoji,
    order = order,
    createdAt = Instant.ofEpochMilli(createdAt),
    updatedAt = Instant.ofEpochMilli(updatedAt),
    deletedAt = deletedAt?.let(Instant::ofEpochMilli),
    rev = rev,
    writerId = writerId,
)

fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id.raw,
    remoteId = remoteId,
    name = name,
    hue = hue,
    emoji = emoji,
    order = order,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    deletedAt = deletedAt?.toEpochMilli(),
    rev = rev,
    writerId = writerId,
)

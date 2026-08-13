package com.notesup.app.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val remoteId: String?,
    val projectId: String?,
    val title: String,
    val blocksJson: String,
    val plaintext: String,
    val pinned: Boolean,
    val locked: Boolean,
    val lockCipher: ByteArray?,
    val tint: Int,
    val paper: String,
    val font: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
    val rev: Long,
    val writerId: String,
    val baseRev: Long,
    val baseWriterId: String?,
)

@Entity(tableName = "notes_fts")
@Fts4
data class NoteFts(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val rowid: Int = 0,
    val noteId: String,
    val plaintext: String,
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val remoteId: String?,
    val name: String,
    val hue: Int,
    val emoji: String?,
    val order: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?,
    val rev: Long,
    val writerId: String,
)

@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: String,
    val noteId: String,
    val path: String,
    val thumbPath: String?,
    val mime: String,
    val width: Int,
    val height: Int,
    val remoteId: String?,
    val updatedAt: Long,
)

@Entity(tableName = "ink")
data class InkEntity(
    @PrimaryKey val id: String,
    val noteId: String,
    val strokeBlob: ByteArray,
    val heightDp: Float,
    val updatedAt: Long,
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val kind: String,
    val localId: String,
    val payloadJson: String,
    val createdAt: Long,
    val attempts: Int,
)

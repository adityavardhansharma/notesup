package com.notesup.app.domain.model

import java.util.UUID
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class NoteId(val raw: String) {
    companion object {
        fun random(): NoteId = NoteId(UUID.randomUUID().toString())
    }
}

@JvmInline
@Serializable
value class ProjectId(val raw: String) {
    companion object {
        fun random(): ProjectId = ProjectId(UUID.randomUUID().toString())
    }
}

@JvmInline
@Serializable
value class BlockId(val raw: String) {
    companion object {
        fun random(): BlockId = BlockId(UUID.randomUUID().toString())
    }
}

@JvmInline
@Serializable
value class MediaId(val raw: String) {
    companion object {
        fun random(): MediaId = MediaId(UUID.randomUUID().toString())
    }
}

@JvmInline
@Serializable
value class UserId(val raw: String)

@JvmInline
@Serializable
value class InkId(val raw: String) {
    companion object {
        fun random(): InkId = InkId(UUID.randomUUID().toString())
    }
}

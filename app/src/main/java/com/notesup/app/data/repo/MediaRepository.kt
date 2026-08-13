package com.notesup.app.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.notesup.app.data.local.MediaDao
import com.notesup.app.data.local.entities.MediaEntity
import com.notesup.app.domain.model.MediaId
import com.notesup.app.domain.model.NoteId
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val media: MediaDao,
) {
    suspend fun import(noteId: NoteId, uri: Uri): MediaEntity? {
        val id = MediaId.random()
        val dir = File(context.filesDir, "media").apply { mkdirs() }
        val dest = File(dir, "${id.raw}.jpg")
        val thumb = File(dir, "${id.raw}_t.jpg")
        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val raw = BitmapFactory.decodeStream(input) ?: return null
                val scaled = scale(raw, 2560)
                FileOutputStream(dest).use { scaled.compress(Bitmap.CompressFormat.JPEG, 88, it) }
                val t = scale(scaled, 400)
                FileOutputStream(thumb).use { t.compress(Bitmap.CompressFormat.JPEG, 80, it) }
                if (scaled !== raw) raw.recycle()
            }
            val entity = MediaEntity(
                id = id.raw,
                noteId = noteId.raw,
                path = dest.absolutePath,
                thumbPath = thumb.absolutePath,
                mime = "image/jpeg",
                width = 0,
                height = 0,
                remoteId = null,
                updatedAt = System.currentTimeMillis(),
            )
            media.upsert(entity)
            entity
        }.getOrNull()
    }

    suspend fun get(id: MediaId): MediaEntity? = media.get(id.raw)

    private fun scale(src: Bitmap, longEdge: Int): Bitmap {
        val w = src.width
        val h = src.height
        val long = maxOf(w, h)
        if (long <= longEdge) return src
        val scale = longEdge.toFloat() / long
        return Bitmap.createScaledBitmap(src, (w * scale).toInt(), (h * scale).toInt(), true)
    }
}

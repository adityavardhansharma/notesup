@file:OptIn(androidx.ink.brush.ExperimentalInkCustomBrushApi::class)

package com.notesup.app.data.repo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.ink.brush.Brush
import androidx.ink.brush.BrushFamily
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.storage.decode
import androidx.ink.storage.encode
import androidx.ink.strokes.Stroke
import androidx.ink.strokes.StrokeInputBatch
import com.notesup.app.data.local.InkDao
import com.notesup.app.data.local.entities.InkEntity
import com.notesup.app.domain.model.InkId
import com.notesup.app.domain.model.NoteId
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InkRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ink: InkDao,
) {
    private val renderer by lazy { CanvasStrokeRenderer.create() }

    suspend fun load(id: InkId): Pair<List<Stroke>, Float>? {
        val entity = ink.get(id.raw) ?: return null
        val strokes = decodeStrokes(entity.strokeBlob)
        return strokes to entity.heightDp
    }

    suspend fun save(id: InkId, noteId: NoteId, strokes: List<Stroke>, heightDp: Float): String? {
        val blob = encodeStrokes(strokes)
        ink.upsert(
            InkEntity(
                id = id.raw,
                noteId = noteId.raw,
                strokeBlob = blob,
                heightDp = heightDp,
                updatedAt = System.currentTimeMillis(),
            ),
        )
        return writePreview(id, strokes, heightDp)
    }

    private fun writePreview(id: InkId, strokes: List<Stroke>, heightDp: Float): String? {
        if (strokes.isEmpty()) return null
        return runCatching {
            val dir = File(context.filesDir, "ink").apply { mkdirs() }
            val file = File(dir, "${id.raw}.png")
            val w = 720
            val h = (heightDp * 2f).toInt().coerceIn(160, 1600)
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val matrix = Matrix()
            strokes.forEach { renderer.draw(canvas, it, matrix) }
            FileOutputStream(file).use { bmp.compress(Bitmap.CompressFormat.PNG, 92, it) }
            bmp.recycle()
            file.absolutePath
        }.getOrNull()
    }

    fun encodeStrokes(strokes: List<Stroke>): ByteArray {
        val bos = ByteArrayOutputStream()
        DataOutputStream(bos).use { out ->
            out.writeInt(1)
            out.writeInt(strokes.size)
            strokes.forEach { stroke ->
                val familyBytes = ByteArrayOutputStream().also { stroke.brush.family.encode(it) }.toByteArray()
                val inputBytes = ByteArrayOutputStream().also { stroke.inputs.encode(it) }.toByteArray()
                out.writeInt(stroke.brush.colorIntArgb)
                out.writeFloat(stroke.brush.size)
                out.writeFloat(stroke.brush.epsilon)
                out.writeInt(familyBytes.size)
                out.write(familyBytes)
                out.writeInt(inputBytes.size)
                out.write(inputBytes)
            }
        }
        return bos.toByteArray()
    }

    fun decodeStrokes(blob: ByteArray): List<Stroke> {
        if (blob.isEmpty()) return emptyList()
        return runCatching {
            DataInputStream(ByteArrayInputStream(blob)).use { input ->
                val version = input.readInt()
                if (version != 1) return emptyList()
                val count = input.readInt()
                List(count) {
                    val color = input.readInt()
                    val size = input.readFloat()
                    val epsilon = input.readFloat()
                    val familyLen = input.readInt()
                    val familyBytes = ByteArray(familyLen)
                    input.readFully(familyBytes)
                    val inputLen = input.readInt()
                    val inputBytes = ByteArray(inputLen)
                    input.readFully(inputBytes)
                    val family = BrushFamily.decode(ByteArrayInputStream(familyBytes))
                    val inputs = StrokeInputBatch.decode(ByteArrayInputStream(inputBytes))
                    Stroke(Brush.createWithColorIntArgb(family, color, size, epsilon), inputs)
                }
            }
        }.getOrDefault(emptyList())
    }
}

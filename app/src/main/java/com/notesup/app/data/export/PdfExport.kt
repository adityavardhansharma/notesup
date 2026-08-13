package com.notesup.app.data.export

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.notesup.app.domain.model.Note
import java.io.File
import java.io.FileOutputStream

object PdfExport {
    fun write(context: Context, note: Note): File {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas
        val paint = android.graphics.Paint().apply { textSize = 18f; isAntiAlias = true }
        canvas.drawText(note.title.ifBlank { "Untitled" }, 56f, 72f, paint)
        paint.textSize = 11f
        var y = 100f
        MarkdownExport.render(note).lines().forEach { line ->
            if (y > 780) return@forEach
            canvas.drawText(line.take(90), 56f, y, paint)
            y += 16f
        }
        doc.finishPage(page)
        val dir = File(context.cacheDir, "export").apply { mkdirs() }
        val file = File(dir, "${note.id.raw}.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()
        return file
    }
}

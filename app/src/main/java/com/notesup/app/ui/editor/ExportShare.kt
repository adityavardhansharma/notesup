package com.notesup.app.ui.editor

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.notesup.app.data.export.MarkdownExport
import com.notesup.app.data.export.PdfExport
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.op.NoteOps
import java.io.File

private fun authority(context: Context) = "${context.packageName}.files"

private fun share(context: Context, intent: Intent) {
    context.startActivity(Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

/** Share the note body as plain text. */
fun shareNoteText(context: Context, note: Note) {
    val body = NoteOps.materializePlaintext(note.title, note.blocks)
    share(
        context,
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, note.title.ifBlank { "Note" })
            putExtra(Intent.EXTRA_TEXT, body)
        },
    )
}

/** Export the note to Markdown and share the .md file. */
fun shareNoteMarkdown(context: Context, note: Note) {
    val dir = File(context.cacheDir, "export").apply { mkdirs() }
    val file = File(dir, "${note.id.raw}.md")
    file.writeText(MarkdownExport.render(note))
    val uri = FileProvider.getUriForFile(context, authority(context), file)
    share(
        context,
        Intent(Intent.ACTION_SEND).apply {
            type = "text/markdown"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        },
    )
}

/** Export the note to a PDF and share it. */
fun shareNotePdf(context: Context, note: Note) {
    val file = PdfExport.write(context, note)
    val uri = FileProvider.getUriForFile(context, authority(context), file)
    share(
        context,
        Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        },
    )
}

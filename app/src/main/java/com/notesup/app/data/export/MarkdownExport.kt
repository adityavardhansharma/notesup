package com.notesup.app.data.export

import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.Note

object MarkdownExport {
    fun render(note: Note): String = buildString {
        append("# ").append(note.title.ifBlank { "Untitled" }).append('\n').append('\n')
        note.blocks.forEach { b ->
            when (b) {
                is Block.Paragraph -> append(b.rich.text).append('\n').append('\n')
                is Block.Heading -> append("#".repeat(b.level)).append(' ').append(b.text).append('\n').append('\n')
                is Block.Quote -> append("> ").append(b.text).append('\n').append('\n')
                is Block.Divider -> append("---\n\n")
                is Block.Checklist -> b.items.forEach { append(if (it.checked) "- [x] " else "- [ ] ").append(it.text).append('\n') }
                is Block.Bullets -> b.items.forEach { append("- ").append(it).append('\n') }
                is Block.Numbered -> b.items.forEachIndexed { i, t -> append("${i + 1}. ").append(t).append('\n') }
                is Block.Code -> append("```").append(b.language).append('\n').append(b.text).append("\n```\n\n")
                is Block.Table -> {
                    if (b.cols <= 0) return@forEach
                    append("| ").append((0 until b.cols).joinToString(" | ") { b.cells.getOrElse(it) { "" } }).append(" |\n")
                    append("| ").append(List(b.cols) { "---" }.joinToString(" | ")).append(" |\n")
                    for (r in 1 until b.rows) {
                        append("| ").append((0 until b.cols).joinToString(" | ") { b.cells.getOrElse(r * b.cols + it) { "" } }).append(" |\n")
                    }
                    append('\n')
                }
                is Block.Image -> append("![").append(b.caption).append("](media/").append(b.mediaId.raw).append(".jpg)\n\n")
                is Block.Ink -> append("![drawing](media/").append(b.inkId.raw).append(".png)\n\n")
            }
        }
    }
}

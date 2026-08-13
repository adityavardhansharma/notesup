package com.notesup.app.domain.op

import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.BlockId
import com.notesup.app.domain.model.CheckItem
import com.notesup.app.domain.model.InkId
import com.notesup.app.domain.model.Note
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.domain.model.RichText
import com.notesup.app.domain.model.plain
import java.util.UUID

object NoteOps {
    fun materializePlaintext(title: String, blocks: List<Block>): String {
        val body = blocks.joinToString("\n") { it.plain() }
        return listOf(title, body).filter { it.isNotBlank() }.joinToString("\n")
    }

    fun starterBlocks(kind: NoteKind): List<Block> = when (kind) {
        NoteKind.TEXT -> listOf(Block.Paragraph(BlockId.random(), RichText.Empty))
        NoteKind.CHECKLIST -> listOf(
            Block.Checklist(
                BlockId.random(),
                listOf(CheckItem(UUID.randomUUID().toString(), "", false)),
            ),
        )
        NoteKind.INK -> listOf(Block.Ink(BlockId.random(), InkId.random(), null))
        NoteKind.IMAGE -> listOf(Block.Paragraph(BlockId.random(), RichText.Empty))
    }

    fun firstImagePath(note: Note): String? = null
}

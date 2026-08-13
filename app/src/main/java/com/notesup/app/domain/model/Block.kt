package com.notesup.app.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheckItem(
    val id: String,
    val text: String,
    val checked: Boolean,
)

@Serializable
sealed interface Block {
    val id: BlockId

    @Serializable
    @SerialName("paragraph")
    data class Paragraph(override val id: BlockId, val rich: RichText) : Block

    @Serializable
    @SerialName("heading")
    data class Heading(override val id: BlockId, val level: Int, val text: String) : Block

    @Serializable
    @SerialName("table")
    data class Table(
        override val id: BlockId,
        val cols: Int,
        val rows: Int,
        val cells: List<String>,
        val headerRow: Boolean,
    ) : Block

    @Serializable
    @SerialName("code")
    data class Code(override val id: BlockId, val language: String, val text: String) : Block

    @Serializable
    @SerialName("quote")
    data class Quote(override val id: BlockId, val text: String) : Block

    @Serializable
    @SerialName("checklist")
    data class Checklist(override val id: BlockId, val items: List<CheckItem>) : Block

    @Serializable
    @SerialName("bullets")
    data class Bullets(override val id: BlockId, val items: List<String>) : Block

    @Serializable
    @SerialName("numbered")
    data class Numbered(override val id: BlockId, val items: List<String>) : Block

    @Serializable
    @SerialName("image")
    data class Image(override val id: BlockId, val mediaId: MediaId, val caption: String) : Block

    @Serializable
    @SerialName("ink")
    data class Ink(override val id: BlockId, val inkId: InkId, val previewPath: String?) : Block

    @Serializable
    @SerialName("divider")
    data class Divider(override val id: BlockId) : Block
}

fun Block.plain(): String = when (this) {
    is Block.Paragraph -> rich.text
    is Block.Heading -> text
    is Block.Table -> cells.joinToString(" ")
    is Block.Code -> text
    is Block.Quote -> text
    is Block.Checklist -> items.joinToString(" ") { it.text }
    is Block.Bullets -> items.joinToString(" ")
    is Block.Numbered -> items.joinToString(" ")
    is Block.Image -> caption
    is Block.Ink -> ""
    is Block.Divider -> ""
}

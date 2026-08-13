package com.notesup.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class SpanStyleTag { BOLD, ITALIC, UNDERLINE, STRIKE, CODE, LINK }

@Serializable
data class RichSpan(
    val start: Int,
    val end: Int,
    val style: SpanStyleTag,
    val href: String? = null,
)

@Serializable
data class RichText(
    val v: Int = 1,
    val text: String,
    val spans: List<RichSpan> = emptyList(),
) {
    companion object {
        val Empty = RichText(text = "")
        fun of(text: String) = RichText(text = text)
    }
}

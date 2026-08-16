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

/** Shift spans after a text edit by locating the changed range. */
fun remapSpans(old: String, new: String, spans: List<RichSpan>): List<RichSpan> {
    if (old == new || spans.isEmpty()) return spans
    var i = 0
    val minLen = minOf(old.length, new.length)
    while (i < minLen && old[i] == new[i]) i++
    var o = old.length
    var n = new.length
    while (o > i && n > i && old[o - 1] == new[n - 1]) {
        o--
        n--
    }
    val delta = (n - i) - (o - i)
    return spans.mapNotNull { s ->
        when {
            s.end <= i -> s
            s.start >= o -> {
                val start = s.start + delta
                val end = s.end + delta
                if (start in 0..new.length && end in start..new.length && end > start) {
                    s.copy(start = start, end = end)
                } else {
                    null
                }
            }
            else -> {
                val start = minOf(s.start, i).coerceIn(0, new.length)
                val end = (s.end + delta).coerceIn(start, new.length)
                if (end > start) s.copy(start = start, end = end) else null
            }
        }
    }
}

fun List<RichSpan>.toggle(start: Int, end: Int, style: SpanStyleTag, href: String? = null): List<RichSpan> {
    if (start >= end) return this
    val covering = filter { it.style == style && it.start <= start && it.end >= end }
    return if (covering.isNotEmpty()) {
        flatMap { span ->
            if (span.style != style || span.end <= start || span.start >= end) {
                listOf(span)
            } else {
                buildList {
                    if (span.start < start) add(span.copy(end = start))
                    if (span.end > end) add(span.copy(start = end))
                }
            }
        }
    } else {
        this + RichSpan(start, end, style, href)
    }
}

fun markdownFromRich(rich: com.notesup.app.domain.model.RichText): String {
    if (rich.spans.isEmpty()) return rich.text
    val marks = Array(rich.text.length + 1) { mutableListOf<Pair<Boolean, RichSpan>>() }
    rich.spans.forEach { span ->
        val s = span.start.coerceIn(0, rich.text.length)
        val e = span.end.coerceIn(s, rich.text.length)
        if (e > s) {
            marks[s].add(false to span)
            marks[e].add(true to span)
        }
    }
    return buildString {
        for (i in 0..rich.text.length) {
            marks[i].filter { it.first }.reversed().forEach { (_, span) ->
                append(span.closeMark())
            }
            marks[i].filter { !it.first }.forEach { (_, span) ->
                append(span.openMark())
            }
            if (i < rich.text.length) append(rich.text[i])
        }
    }
}

private fun RichSpan.openMark(): String = when (style) {
    SpanStyleTag.BOLD -> "**"
    SpanStyleTag.ITALIC -> "*"
    SpanStyleTag.UNDERLINE -> "<u>"
    SpanStyleTag.STRIKE -> "~~"
    SpanStyleTag.CODE -> "`"
    SpanStyleTag.LINK -> "["
}

private fun RichSpan.closeMark(): String = when (style) {
    SpanStyleTag.BOLD -> "**"
    SpanStyleTag.ITALIC -> "*"
    SpanStyleTag.UNDERLINE -> "</u>"
    SpanStyleTag.STRIKE -> "~~"
    SpanStyleTag.CODE -> "`"
    SpanStyleTag.LINK -> "](${href.orEmpty()})"
}

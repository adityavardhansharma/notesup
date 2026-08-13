package com.notesup.app.domain.op

data class ParsedSearch(
    val text: String,
    val project: String?,
    val verb: Verb?,
) {
    enum class Verb { PIN, INK, IMAGE, LOCKED }
}

object SearchQuery {
    fun parse(raw: String): ParsedSearch {
        var rest = raw.trim()
        var project: String? = null
        val projectMatch = Regex("""project:(\S+)""", RegexOption.IGNORE_CASE).find(rest)
        if (projectMatch != null) {
            project = projectMatch.groupValues[1]
            rest = rest.replace(projectMatch.value, "").trim()
        }
        val verb = when (rest.lowercase()) {
            "pin", "pinned" -> ParsedSearch.Verb.PIN
            "ink", "drawings", "drawing" -> ParsedSearch.Verb.INK
            "image", "images" -> ParsedSearch.Verb.IMAGE
            "locked", "lock" -> ParsedSearch.Verb.LOCKED
            else -> null
        }
        val text = if (verb != null) "" else rest
        return ParsedSearch(text = text, project = project, verb = verb)
    }
}

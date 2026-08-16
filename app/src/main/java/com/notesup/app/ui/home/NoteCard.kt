package com.notesup.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.notesup.app.domain.model.Note
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.theme.TintWashes
import java.time.Duration
import java.time.Instant

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    showProject: String? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val fill = when {
        note.pinned -> scheme.tertiaryContainer
        note.tint in 1..7 -> scheme.surfaceContainerLow
        else -> scheme.surfaceContainerLow
    }
    val wash = if (note.tint in 1..7) TintWashes[note.tint].copy(alpha = 0.08f) else Color.Transparent
    Column(
        modifier
            .clip(RoundedCornerShape(24.dp))
            .background(fill)
            .background(wash)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .heightIn(min = 148.dp),
    ) {
        val cover = note.blocks.filterIsInstance<com.notesup.app.domain.model.Block.Image>().firstOrNull()?.let { img ->
            val context = androidx.compose.ui.platform.LocalContext.current
            val thumb = java.io.File(context.filesDir, "media/${img.mediaId.raw}_t.jpg")
            val file = java.io.File(context.filesDir, "media/${img.mediaId.raw}.jpg")
            if (thumb.exists()) thumb else file
        } ?: note.blocks.filterIsInstance<com.notesup.app.domain.model.Block.Ink>().firstOrNull()?.previewPath?.let { java.io.File(it) }
        cover?.let { file ->
            AsyncImage(
                model = file,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 120.dp)
                    .aspectRatio(16f / 10f, matchHeightConstraintsFirst = true)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            )
        }
        Box(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    if (note.locked) note.title.ifBlank { "Locked note" } else note.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    if (note.locked) "Locked note" else note.preview,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    listOfNotNull(relative(note.updatedAt), showProject).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSurfaceVariant,
                )
            }
            Row(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
            ) {
                if (note.locked) NuIcon(NotesupIcons.Lock, null, Modifier.size(16.dp))
                if (note.pinned) NuIcon(NotesupIcons.Pin, null, Modifier.size(16.dp), tint = scheme.onTertiaryContainer)
            }
        }
    }
}

@Composable
fun NoteListRow(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    supporting: String? = null,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(scheme.surfaceContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                note.displayTitle.take(1).uppercase(),
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(note.displayTitle, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                supporting ?: if (note.locked) "Locked note" else note.preview,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(relative(note.updatedAt), style = MaterialTheme.typography.labelSmall, color = scheme.onSurfaceVariant)
    }
}

fun relative(instant: Instant): String {
    val d = Duration.between(instant, Instant.now())
    val min = d.toMinutes()
    return when {
        min < 1 -> "now"
        min < 60 -> "${min}m"
        min < 60 * 24 -> "${d.toHours()}h"
        else -> "${d.toDays()}d"
    }
}

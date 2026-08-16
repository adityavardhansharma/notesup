package com.notesup.app.ui.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notesup.app.R

private val CommonEmoji = listOf(
    "📁", "📌", "✏️", "📝", "💡", "🏠", "💼", "📚",
    "🧠", "🎯", "🌱", "⭐", "❤️", "🔥", "🌙", "☀️",
    "🎵", "📷", "✈️", "🛒", "🍳", "💪", "🎨", "🧪",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPickerSheet(onDismiss: () -> Unit, onPick: (String?) -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(20.dp).padding(bottom = 24.dp)) {
            Text(stringResource(R.string.add_emoji), style = MaterialTheme.typography.titleSmall)
            Text(
                stringResource(R.string.none_emoji),
                modifier = Modifier.fillMaxWidth().clickable { onPick(null) }.padding(vertical = 12.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(CommonEmoji) { e ->
                    Box(
                        Modifier.size(40.dp).clickable { onPick(e) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(e, fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

package com.notesup.app.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesup.app.R
import com.notesup.app.domain.model.Project
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.theme.TintWashes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoveToProjectSheet(
    projects: List<Project>,
    onDismiss: () -> Unit,
    onPick: (String?) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(bottom = 24.dp)) {
            Text(
                stringResource(R.string.move_to),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(20.dp, 8.dp),
            )
            Row(
                Modifier.fillMaxWidth().height(56.dp).clickable { onPick(null) }.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NuIcon(NotesupIcons.Inbox, null)
                Spacer(Modifier.width(12.dp))
                Text(stringResource(R.string.inbox), style = MaterialTheme.typography.titleMedium)
            }
            LazyColumn {
                items(projects, key = { it.id.raw }) { p ->
                    Row(
                        Modifier.fillMaxWidth().height(56.dp).clickable { onPick(p.id.raw) }.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier.size(12.dp).clip(CircleShape)
                                .background(if (p.hue in 1..7) TintWashes[p.hue] else MaterialTheme.colorScheme.outline),
                        )
                        Spacer(Modifier.width(12.dp))
                        Text((p.emoji ?: "") + p.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

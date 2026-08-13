package com.notesup.app.ui.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesup.app.R
import com.notesup.app.ui.theme.Stadium
import com.notesup.app.ui.theme.TintWashes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectEditSheet(
    onDismiss: () -> Unit,
    onSave: (name: String, hue: Int, emoji: String?) -> Unit,
    initialName: String = "",
    initialHue: Int = 0,
    initialEmoji: String? = null,
    title: String? = null,
) {
    var name by remember { mutableStateOf(initialName) }
    var hue by remember { mutableIntStateOf(initialHue) }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState()) {
        Column(Modifier.padding(20.dp).padding(bottom = 24.dp)) {
            Text(title ?: stringResource(R.string.new_project), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.length <= 32) name = it },
                label = { Text(stringResource(R.string.project_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                (0..7).forEach { i ->
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (i == 0) MaterialTheme.colorScheme.outline else TintWashes[i])
                            .clickable { hue = i },
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onSave(name.trim(), hue, initialEmoji) },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = Stadium,
            ) { Text(stringResource(R.string.create)) }
        }
    }
}

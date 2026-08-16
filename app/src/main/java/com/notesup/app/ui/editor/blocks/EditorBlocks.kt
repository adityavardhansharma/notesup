package com.notesup.app.ui.editor.blocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.notesup.app.R
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.CheckItem
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.common.rememberHaptics
import java.io.File

@Composable
fun ChecklistEditor(
    block: Block.Checklist,
    sortChecked: Boolean,
    bodyStyle: TextStyle,
    fieldFor: (CheckItem) -> TextFieldState,
    onToggle: (String) -> Unit,
    onAdd: () -> Unit,
    onAddAfter: (String) -> Unit,
    onRemove: (String) -> Unit,
    onFocus: (String) -> Unit,
    onMove: (from: Int, to: Int) -> Unit = { _, _ -> },
) {
    val haptics = rememberHaptics()
    val items = if (sortChecked) block.items.filter { !it.checked } + block.items.filter { it.checked } else block.items
    Column(Modifier.padding(vertical = 4.dp)) {
        items.forEachIndexed { index, item ->
            val field = fieldFor(item)
            var focused by remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                NuIcon(NotesupIcons.Drag, null, Modifier.size(18.dp).clickable {
                    if (index > 0) onMove(index, index - 1)
                })
                Checkbox(
                    checked = item.checked,
                    onCheckedChange = { haptics.tick(); onToggle(item.id) },
                )
                BasicTextField(
                    state = field,
                    textStyle = bodyStyle.copy(
                        color = if (item.checked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    onKeyboardAction = KeyboardActionHandler { onAddAfter(item.id) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp)
                        .onFocusChanged {
                            focused = it.isFocused
                            if (it.isFocused) onFocus("c:${item.id}")
                        }
                        .onPreviewKeyEvent { ev ->
                            if (ev.type == KeyEventType.KeyUp && ev.key == Key.Backspace && field.text.isEmpty()) {
                                onRemove(item.id)
                                true
                            } else {
                                false
                            }
                        },
                )
                if (focused) {
                    androidx.compose.material3.IconButton(onClick = { onRemove(item.id) }) {
                        NuIcon(NotesupIcons.Close, stringResource(R.string.cd_close), Modifier.size(18.dp))
                    }
                }
            }
        }
        TextButton(onClick = onAdd, modifier = Modifier.padding(start = 24.dp)) {
            NuIcon(NotesupIcons.Add, null, Modifier.size(18.dp))
            Text(stringResource(R.string.add_item), modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun ListEditor(
    items: List<String>,
    numbered: Boolean,
    bodyStyle: TextStyle,
    fieldFor: (Int) -> TextFieldState,
    onAdd: () -> Unit,
    onAddAfter: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onFocus: (Int) -> Unit,
) {
    Column(Modifier.padding(vertical = 4.dp)) {
        items.forEachIndexed { i, _ ->
            val field = fieldFor(i)
            var focused by remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (numbered) "${i + 1}.  " else "•  ", style = bodyStyle)
                BasicTextField(
                    state = field,
                    textStyle = bodyStyle.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    onKeyboardAction = KeyboardActionHandler { onAddAfter(i) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .onFocusChanged {
                            focused = it.isFocused
                            if (it.isFocused) onFocus(i)
                        }
                        .onPreviewKeyEvent { ev ->
                            if (ev.type == KeyEventType.KeyUp && ev.key == Key.Backspace && field.text.isEmpty()) {
                                onRemove(i)
                                true
                            } else false
                        },
                )
                if (focused) {
                    androidx.compose.material3.IconButton(onClick = { onRemove(i) }) {
                        NuIcon(NotesupIcons.Close, stringResource(R.string.cd_close), Modifier.size(18.dp))
                    }
                }
            }
        }
        TextButton(onClick = onAdd, modifier = Modifier.padding(start = 24.dp)) {
            NuIcon(NotesupIcons.Add, null, Modifier.size(18.dp))
            Text(stringResource(R.string.add_item), modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun TableEditor(
    block: Block.Table,
    bodyStyle: TextStyle,
    fieldFor: (Int) -> TextFieldState,
    onAddRow: () -> Unit,
    onAddCol: () -> Unit,
    onRemoveRow: () -> Unit,
    onRemoveCol: () -> Unit,
    onToggleHeader: () -> Unit,
    onFocus: (Int) -> Unit,
) {
    var anyFocus by remember { mutableStateOf(false) }
    val scheme = MaterialTheme.colorScheme
    Column(Modifier.padding(vertical = 8.dp)) {
        Column(Modifier.horizontalScroll(rememberScrollState())) {
            repeat(block.rows) { r ->
                Row {
                    repeat(block.cols) { c ->
                        val i = r * block.cols + c
                        val header = block.headerRow && r == 0
                        BasicTextField(
                            state = fieldFor(i),
                            textStyle = if (header) MaterialTheme.typography.titleSmall.copy(color = scheme.onSurface) else bodyStyle.copy(color = scheme.onSurface),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .heightIn(min = 40.dp)
                                .border(1.dp, scheme.outlineVariant)
                                .background(if (header) scheme.surfaceContainerHigh else scheme.surface)
                                .padding(8.dp)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        anyFocus = true
                                        onFocus(i)
                                    }
                                },
                            decorator = { inner ->
                                androidx.compose.foundation.layout.Box(Modifier.heightIn(min = 24.dp)) { inner() }
                            },
                        )
                    }
                }
            }
        }
        if (anyFocus) {
            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onAddRow) { Text(stringResource(R.string.add_row)) }
                TextButton(onClick = onAddCol) { Text(stringResource(R.string.add_col)) }
                TextButton(onClick = onRemoveRow) { Text(stringResource(R.string.remove_row)) }
                TextButton(onClick = onRemoveCol) { Text(stringResource(R.string.remove_col)) }
                FilterChip(
                    selected = block.headerRow,
                    onClick = onToggleHeader,
                    label = { Text(stringResource(R.string.header_row)) },
                )
            }
        }
    }
}

@Composable
fun ImageEditor(
    block: Block.Image,
    filesDir: File,
    captionField: TextFieldState,
    onView: () -> Unit,
    onReplace: () -> Unit,
    onRemove: () -> Unit,
    onFocus: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    val file = remember(block.mediaId.raw) { File(filesDir, "media/${block.mediaId.raw}.jpg") }
    val scheme = MaterialTheme.colorScheme
    Column(Modifier.padding(vertical = 8.dp)) {
        AsyncImage(
            model = file,
            contentDescription = block.caption.ifBlank { null },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(onClick = onView, onLongClick = { menu = true }),
        )
        DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
            DropdownMenuItem(text = { Text(stringResource(R.string.view_image)) }, onClick = { menu = false; onView() })
            DropdownMenuItem(text = { Text(stringResource(R.string.replace)) }, onClick = { menu = false; onReplace() })
            DropdownMenuItem(text = { Text(stringResource(R.string.remove_image)) }, onClick = { menu = false; onRemove() })
        }
        BasicTextField(
            state = captionField,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            ),
            cursorBrush = SolidColor(scheme.primary),
            lineLimits = TextFieldLineLimits.SingleLine,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .onFocusChanged { if (it.isFocused) onFocus() },
            decorator = { inner ->
                androidx.compose.foundation.layout.Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (captionField.text.isEmpty()) {
                        Text(
                            stringResource(R.string.add_caption),
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                    inner()
                }
            },
        )
    }
}

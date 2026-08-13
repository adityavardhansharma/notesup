package com.notesup.app.ui.editor

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesup.app.R
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.BlockId
import com.notesup.app.domain.model.CheckItem
import com.notesup.app.domain.model.RichText
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.common.rememberHaptics
import com.notesup.app.ui.lock.LockGateScreen
import com.notesup.app.ui.theme.bodyNoteStyle
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    noteId: String,
    vm: EditorViewModel,
    created: Boolean,
    onBack: () -> Unit,
    onMissing: () -> Unit,
) {
    LaunchedEffect(noteId) { vm.attach(noteId) }
    val note by vm.note.collectAsStateWithLifecycle()
    val haptics = rememberHaptics()
    var menu by remember { mutableStateOf(false) }
    var insert by remember { mutableStateOf(false) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    val bodyFocus = remember { FocusRequester() }
    val size by vm.prefs.bodySize.collectAsStateWithLifecycle("M")

    LaunchedEffect(created) {
        if (created) {
            delay(30)
            runCatching { bodyFocus.requestFocus() }
        }
    }

    PredictiveBackHandler(true) { progress ->
        try {
            progress.collect { ev -> backProgress = ev.progress }
            vm.autosave()
            onBack()
        } catch (e: kotlinx.coroutines.CancellationException) {
            backProgress = 0f
            throw e
        }
    }

    val n = note
    if (n == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_missing), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    if (n.locked && n.lockCipher != null) {
        LockGateScreen(title = n.title, onBack = onBack, onUnlocked = { vm.setLocked(false, null) })
        return
    }

    val scheme = MaterialTheme.colorScheme
    Column(
        Modifier
            .fillMaxSize()
            .background(scheme.background)
            .statusBarsPadding()
            .imePadding(),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { vm.autosave(); onBack() }) {
                NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back))
            }
            Box(Modifier.weight(1f))
            IconButton(onClick = { menu = true }) {
                NuIcon(NotesupIcons.More, stringResource(R.string.cd_more))
            }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                DropdownMenuItem(text = { Text(if (n.pinned) stringResource(R.string.menu_unpin) else stringResource(R.string.menu_pin)) }, onClick = { haptics.confirm(); vm.togglePin(); menu = false })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_color)) }, onClick = { menu = false })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_paper)) }, onClick = { menu = false })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_type)) }, onClick = { menu = false })
                DropdownMenuItem(text = { Text(if (n.locked) stringResource(R.string.menu_unlock) else stringResource(R.string.menu_lock)) }, onClick = { vm.setLocked(!n.locked); menu = false })
                HorizontalDivider()
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_md)) }, onClick = { menu = false })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_pdf)) }, onClick = { menu = false })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_share)) }, onClick = { menu = false })
                HorizontalDivider()
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_delete), color = scheme.error) }, onClick = {
                    haptics.reject(); vm.delete(); menu = false; onBack()
                })
            }
        }

        LazyColumn(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            item {
                BasicTextField(
                    state = vm.titleState,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(color = scheme.onSurface),
                    cursorBrush = SolidColor(scheme.primary),
                    lineLimits = TextFieldLineLimits.MultiLine(1, 4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 640.dp)
                        .padding(top = 12.dp, bottom = 16.dp),
                    decorator = { inner ->
                        Box {
                            if (vm.titleState.text.isEmpty()) {
                                Text(
                                    stringResource(R.string.title_hint),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = scheme.onSurfaceVariant.copy(alpha = 0.55f),
                                )
                            }
                            inner()
                        }
                    },
                )
            }
            items(n.blocks, key = { it.id.raw }) { block ->
                when (block) {
                    is Block.Paragraph -> {
                        val field = vm.fieldFor(block)
                        BasicTextField(
                            state = field,
                            textStyle = bodyNoteStyle(size).copy(color = scheme.onSurface),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .then(if (n.blocks.indexOf(block) == 0) Modifier.focusRequester(bodyFocus) else Modifier),
                        )
                    }
                    is Block.Heading -> Text(
                        block.text.ifBlank { "Heading" },
                        style = when (block.level) {
                            1 -> MaterialTheme.typography.titleLarge
                            2 -> MaterialTheme.typography.titleMedium
                            else -> MaterialTheme.typography.titleSmall
                        },
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                    is Block.Quote -> Row {
                        Box(Modifier.padding(end = 12.dp).height(24.dp).background(scheme.primary).padding(start = 3.dp))
                        Text(block.text, style = bodyNoteStyle(size))
                    }
                    is Block.Divider -> HorizontalDivider(Modifier.padding(vertical = 16.dp), color = scheme.outlineVariant)
                    is Block.Checklist -> Column(Modifier.padding(vertical = 4.dp)) {
                        block.items.forEach { item ->
                            Text("○  ${item.text}", style = bodyNoteStyle(size), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                    is Block.Bullets -> block.items.forEach { Text("•  $it", style = bodyNoteStyle(size)) }
                    is Block.Numbered -> block.items.forEachIndexed { i, t -> Text("${i + 1}.  $t", style = bodyNoteStyle(size)) }
                    is Block.Code -> Box(
                        Modifier
                            .fillMaxWidth()
                            .background(scheme.surfaceContainer, MaterialTheme.shapes.large)
                            .padding(12.dp),
                    ) { Text(block.text, style = bodyNoteStyle(size)) }
                    is Block.Table -> Text("▦ ${block.rows} × ${block.cols}", style = MaterialTheme.typography.bodyMedium)
                    is Block.Image -> Text(block.caption.ifBlank { "image" }, color = scheme.onSurfaceVariant)
                    is Block.Ink -> Box(
                        Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .background(scheme.surfaceContainerLow, MaterialTheme.shapes.large),
                    )
                }
            }
        }

        FormatToolbar(
            onInsert = { insert = true },
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 8.dp),
        )
    }

    if (insert) {
        ModalBottomSheet(onDismissRequest = { insert = false }, sheetState = rememberModalBottomSheetState()) {
            InsertRows { kind ->
                insert = false
                val id = BlockId.random()
                val block = when (kind) {
                    "h1" -> Block.Heading(id, 1, "")
                    "h2" -> Block.Heading(id, 2, "")
                    "h3" -> Block.Heading(id, 3, "")
                    "check" -> Block.Checklist(id, listOf(CheckItem(UUID.randomUUID().toString(), "", false)))
                    "bullets" -> Block.Bullets(id, listOf(""))
                    "num" -> Block.Numbered(id, listOf(""))
                    "quote" -> Block.Quote(id, "")
                    "div" -> Block.Divider(id)
                    "code" -> Block.Code(id, "plain", "")
                    "table" -> Block.Table(id, 2, 2, List(4) { "" }, true)
                    else -> Block.Paragraph(id, RichText.Empty)
                }
                vm.insert(block)
            }
        }
    }
}

@Composable
private fun FormatToolbar(onInsert: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, MaterialTheme.shapes.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = {}) { NuIcon(NotesupIcons.Bold, null) }
        IconButton(onClick = {}) { NuIcon(NotesupIcons.Italic, null) }
        IconButton(onClick = {}) { NuIcon(NotesupIcons.Underline, null) }
        IconButton(onClick = onInsert) { NuIcon(NotesupIcons.Add, stringResource(R.string.insert)) }
    }
}

@Composable
private fun InsertRows(onPick: (String) -> Unit) {
    Column(Modifier.padding(bottom = 24.dp)) {
        listOf(
            "text" to R.string.text,
            "h1" to R.string.heading_1,
            "check" to R.string.checklist,
            "bullets" to R.string.bullets,
            "table" to R.string.table,
            "image" to R.string.type_image,
            "ink" to R.string.drawing,
            "code" to R.string.code_block,
            "quote" to R.string.quote,
            "div" to R.string.divider,
        ).forEach { (k, s) ->
            Text(
                stringResource(s),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 14.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
            androidx.compose.foundation.layout.Box(
                Modifier
                    .fillMaxWidth()
                    .height(1.dp),
            )
        }
    }
}

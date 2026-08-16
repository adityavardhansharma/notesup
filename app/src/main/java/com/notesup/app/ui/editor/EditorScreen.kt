package com.notesup.app.ui.editor

import android.net.Uri
import androidx.activity.compose.PredictiveBackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.notesup.app.R
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.BlockId
import com.notesup.app.domain.model.CheckItem
import com.notesup.app.domain.model.RichText
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.common.rememberHaptics
import com.notesup.app.ui.lock.LockGateScreen
import com.notesup.app.ui.theme.TintWashes
import com.notesup.app.ui.theme.bodyNoteStyle
import com.notesup.app.ui.theme.noteFontFamily
import java.io.File
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var menu by remember { mutableStateOf(false) }
    var insert by remember { mutableStateOf(false) }
    var imageSource by remember { mutableStateOf(false) }
    var colorSheet by remember { mutableStateOf(false) }
    var paperSheet by remember { mutableStateOf(false) }
    var typeSheet by remember { mutableStateOf(false) }
    var unlocked by remember { mutableStateOf(false) }
    var backProgress by remember { mutableFloatStateOf(0f) }
    val bodyFocus = remember { FocusRequester() }
    val size by vm.prefs.bodySize.collectAsStateWithLifecycle("M")

    var pendingPhoto by remember { mutableStateOf<Uri?>(null) }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.insertImage(uri)
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        pendingPhoto?.let { if (ok) vm.insertImage(it) }
    }

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
    if (n.locked && !unlocked) {
        LockGateScreen(title = n.title, onBack = onBack, onUnlocked = { unlocked = true })
        return
    }

    val scheme = MaterialTheme.colorScheme
    val fontFamily = remember(n.font) { noteFontFamily(n.font) }
    val tintWash = if (n.tint in 1..7) TintWashes[n.tint].copy(alpha = 0.06f) else Color.Transparent
    val ruleColor = scheme.outlineVariant.copy(alpha = 0.6f)

    Column(
        Modifier
            .fillMaxSize()
            .background(scheme.background)
            .background(tintWash)
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
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_color)) }, onClick = { menu = false; colorSheet = true })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_paper)) }, onClick = { menu = false; paperSheet = true })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_type)) }, onClick = { menu = false; typeSheet = true })
                DropdownMenuItem(text = { Text(if (n.locked) stringResource(R.string.menu_unlock) else stringResource(R.string.menu_lock)) }, onClick = { vm.setLocked(!n.locked); if (!n.locked) unlocked = true; menu = false })
                HorizontalDivider()
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_md)) }, onClick = { menu = false; scope.launch { shareNoteMarkdown(context, vm.persistNow() ?: n) } })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_pdf)) }, onClick = { menu = false; scope.launch { shareNotePdf(context, vm.persistNow() ?: n) } })
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_share)) }, onClick = { menu = false; scope.launch { shareNoteText(context, vm.persistNow() ?: n) } })
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
                .paperBackground(n.paper, ruleColor)
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
                            textStyle = bodyNoteStyle(size, fontFamily).copy(color = scheme.onSurface),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .then(if (n.blocks.indexOf(block) == 0) Modifier.focusRequester(bodyFocus) else Modifier),
                        )
                    }
                    is Block.Heading -> {
                        val field = vm.fieldForHeading(block)
                        BasicTextField(
                            state = field,
                            textStyle = when (block.level) {
                                1 -> MaterialTheme.typography.titleLarge
                                2 -> MaterialTheme.typography.titleMedium
                                else -> MaterialTheme.typography.titleSmall
                            }.copy(color = scheme.onSurface),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        )
                    }
                    is Block.Quote -> Row(Modifier.padding(vertical = 4.dp)) {
                        Box(Modifier.padding(end = 12.dp).width(3.dp).heightIn(min = 24.dp).background(scheme.primary))
                        BasicTextField(
                            state = vm.fieldForQuote(block),
                            textStyle = bodyNoteStyle(size, fontFamily).copy(color = scheme.onSurfaceVariant),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier.weight(1f),
                        )
                    }
                    is Block.Divider -> HorizontalDivider(Modifier.padding(vertical = 16.dp), color = scheme.outlineVariant)
                    is Block.Checklist -> Column(Modifier.padding(vertical = 4.dp)) {
                        block.items.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = item.checked,
                                    onCheckedChange = { haptics.tick(); vm.toggleCheck(block.id.raw, item.id) },
                                )
                                val field = vm.fieldForCheck(item)
                                BasicTextField(
                                    state = field,
                                    textStyle = bodyNoteStyle(size, fontFamily).copy(
                                        color = if (item.checked) scheme.onSurfaceVariant else scheme.onSurface,
                                        textDecoration = if (item.checked) TextDecoration.LineThrough else null,
                                    ),
                                    cursorBrush = SolidColor(scheme.primary),
                                    modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                                )
                            }
                        }
                        AddItemButton { vm.addCheckItem(block.id.raw) }
                    }
                    is Block.Bullets -> Column(Modifier.padding(vertical = 4.dp)) {
                        block.items.forEachIndexed { i, _ ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("•  ", style = bodyNoteStyle(size, fontFamily))
                                BasicTextField(
                                    state = vm.fieldForBullet(block, i),
                                    textStyle = bodyNoteStyle(size, fontFamily).copy(color = scheme.onSurface),
                                    cursorBrush = SolidColor(scheme.primary),
                                    modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                )
                            }
                        }
                        AddItemButton { vm.addListItem(block.id.raw) }
                    }
                    is Block.Numbered -> Column(Modifier.padding(vertical = 4.dp)) {
                        block.items.forEachIndexed { i, _ ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${i + 1}.  ", style = bodyNoteStyle(size, fontFamily))
                                BasicTextField(
                                    state = vm.fieldForNumber(block, i),
                                    textStyle = bodyNoteStyle(size, fontFamily).copy(color = scheme.onSurface),
                                    cursorBrush = SolidColor(scheme.primary),
                                    modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                )
                            }
                        }
                        AddItemButton { vm.addListItem(block.id.raw) }
                    }
                    is Block.Code -> Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(scheme.surfaceContainer, MaterialTheme.shapes.large)
                            .padding(12.dp),
                    ) {
                        BasicTextField(
                            state = vm.fieldForCode(block),
                            textStyle = bodyNoteStyle(size, com.notesup.app.ui.theme.JetBrainsMono).copy(color = scheme.onSurface),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    is Block.Table -> Text("▦ ${block.rows} × ${block.cols}", style = MaterialTheme.typography.bodyMedium)
                    is Block.Image -> {
                        val file = remember(block.mediaId.raw) { File(context.filesDir, "media/${block.mediaId.raw}.jpg") }
                        AsyncImage(
                            model = file,
                            contentDescription = block.caption.ifBlank { null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp)),
                        )
                    }
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
                if (kind == "image") {
                    imageSource = true
                    return@InsertRows
                }
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

    if (imageSource) {
        com.notesup.app.ui.media.ImageSourceSheet(
            onDismiss = { imageSource = false },
            onCamera = {
                imageSource = false
                val dir = File(context.cacheDir, "captures").apply { mkdirs() }
                val file = File(dir, "cap_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.files", file)
                pendingPhoto = uri
                runCatching { camera.launch(uri) }
            },
            onGallery = {
                imageSource = false
                gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
        )
    }

    if (colorSheet) {
        OptionSheet(onDismiss = { colorSheet = false }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                (0..7).forEach { i ->
                    Box(
                        Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (i == 0) scheme.surfaceContainerHighest else TintWashes[i])
                            .clickable { vm.setTint(i); colorSheet = false },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (i == n.tint) NuIcon(NotesupIcons.Check, null, tint = scheme.onSurface)
                    }
                }
            }
        }
    }

    if (paperSheet) {
        OptionSheet(onDismiss = { paperSheet = false }) {
            listOf(
                "blank" to R.string.paper_blank,
                "lines" to R.string.paper_lines,
                "dots" to R.string.paper_dots,
                "grid" to R.string.paper_grid,
            ).forEach { (key, label) ->
                OptionRow(stringResource(label), selected = n.paper == key) { vm.setPaper(key); paperSheet = false }
            }
        }
    }

    if (typeSheet) {
        OptionSheet(onDismiss = { typeSheet = false }) {
            listOf(
                "roboto_flex" to R.string.font_roboto,
                "literata" to R.string.font_literata,
                "jetbrains_mono" to R.string.font_mono,
                "atkinson" to R.string.font_atkinson,
            ).forEach { (key, label) ->
                OptionRow(stringResource(label), selected = n.font == key) { vm.setFont(key); typeSheet = false }
            }
        }
    }
}

private fun Modifier.paperBackground(paper: String, color: Color): Modifier = drawBehind {
    val gap = 30.dp.toPx()
    val stroke = 1f
    when (paper) {
        "lines" -> {
            var y = gap
            while (y < size.height) {
                drawLine(color, Offset(0f, y), Offset(size.width, y), stroke)
                y += gap
            }
        }
        "grid" -> {
            var y = gap
            while (y < size.height) {
                drawLine(color, Offset(0f, y), Offset(size.width, y), stroke)
                y += gap
            }
            var x = gap
            while (x < size.width) {
                drawLine(color, Offset(x, 0f), Offset(x, size.height), stroke)
                x += gap
            }
        }
        "dots" -> {
            var y = gap
            while (y < size.height) {
                var x = gap
                while (x < size.width) {
                    drawCircle(color, 1.6f, Offset(x, y))
                    x += gap
                }
                y += gap
            }
        }
        else -> Unit
    }
}

@Composable
private fun AddItemButton(onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.padding(start = 24.dp)) {
        NuIcon(NotesupIcons.Add, null, Modifier.size(18.dp))
        Text(stringResource(R.string.add_item), modifier = Modifier.padding(start = 8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState()) {
        Column(Modifier.fillMaxWidth().padding(bottom = 24.dp)) { content() }
    }
}

@Composable
private fun OptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (selected) NuIcon(NotesupIcons.Check, null, tint = MaterialTheme.colorScheme.primary)
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
            "code" to R.string.code_block,
            "quote" to R.string.quote,
            "div" to R.string.divider,
        ).forEach { (k, s) ->
            Text(
                stringResource(s),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPick(k) }
                    .padding(20.dp, 14.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

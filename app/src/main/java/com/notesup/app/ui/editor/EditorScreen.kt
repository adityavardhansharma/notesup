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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.notesup.app.domain.model.SpanStyleTag
import com.notesup.app.ui.editor.blocks.ChecklistEditor
import com.notesup.app.ui.editor.blocks.ImageEditor
import com.notesup.app.ui.editor.blocks.ListEditor
import com.notesup.app.ui.editor.blocks.TableEditor
import com.notesup.app.ui.media.Lightbox
import com.notesup.app.ui.theme.JetBrainsMono
import com.notesup.app.ui.theme.paperBackground
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
    val focusMode by vm.prefs.focus.collectAsStateWithLifecycle("auto")
    val sortChecked by vm.prefs.sortChecked.collectAsStateWithLifecycle(false)
    val styleEpoch by vm.styleEpoch.collectAsStateWithLifecycle()
    val focusedField by vm.focusedField.collectAsStateWithLifecycle()
    val projects by vm.projectsFlow.collectAsStateWithLifecycle()
    var moveSheet by remember { mutableStateOf(false) }
    var lightboxFile by remember { mutableStateOf<File?>(null) }
    var linkDialog by remember { mutableStateOf(false) }
    var replaceImageId by remember { mutableStateOf<String?>(null) }
    var cameraError by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    var viewportH by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    var pendingPhoto by remember { mutableStateOf<Uri?>(null) }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val replace = replaceImageId
            if (replace != null) vm.replaceImage(replace, uri) else vm.insertImage(uri)
            replaceImageId = null
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        pendingPhoto?.let {
            if (ok) {
                val replace = replaceImageId
                if (replace != null) vm.replaceImage(replace, it) else vm.insertImage(it)
                replaceImageId = null
            }
        }
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
    if (n.locked && !unlocked && !created) {
        LockGateScreen(title = n.title, onBack = onBack, onUnlocked = { vm.unlock(); unlocked = true })
        return
    }

    val focusedId = focusedField?.substringAfter(':')?.substringBefore(':')
    LaunchedEffect(focusedField, focusMode) {
        if (focusMode != "typewriter") return@LaunchedEffect
        val idx = n.blocks.indexOfFirst { it.id.raw == focusedId }
        if (idx >= 0) runCatching { listState.animateScrollToItem(idx + 1) }
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
                DropdownMenuItem(text = { Text(stringResource(R.string.menu_move)) }, onClick = { menu = false; moveSheet = true })
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

        val pad = if (focusMode == "typewriter") {
            val h = with(density) { (viewportH * 0.4f).toDp() }
            PaddingValues(start = 20.dp, end = 20.dp, top = h, bottom = h)
        } else {
            PaddingValues(horizontal = 20.dp)
        }
        LazyColumn(
            state = listState,
            contentPadding = pad,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onSizeChanged { viewportH = it.height }
                .paperBackground(n.paper, ruleColor),
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
                val dim = (focusMode == "auto" || focusMode == "sentence") &&
                    focusedId != null && focusedId != block.id.raw
                Box(Modifier.graphicsLayer { alpha = if (dim) 0.35f else 1f }) {
                    when (block) {
                    is Block.Paragraph -> {
                        val field = vm.fieldFor(block)
                        val key = "p:${block.id.raw}"
                        val spans = vm.spansFor(key)
                        @Suppress("UNUSED_EXPRESSION")
                        styleEpoch
                        BasicTextField(
                            state = field,
                            textStyle = bodyNoteStyle(size, fontFamily).copy(color = scheme.onSurface),
                            cursorBrush = SolidColor(scheme.primary),
                            outputTransformation = androidx.compose.foundation.text.input.OutputTransformation {
                                spans.forEach { span ->
                                    val start = span.start.coerceIn(0, length)
                                    val end = span.end.coerceIn(start, length)
                                    if (end > start) addStyle(span.toComposeStyle(scheme, fontFamily), start, end)
                                }
                                if (focusMode == "sentence" && focusedField == key) {
                                    val caret = field.selection.start.coerceIn(0, length)
                                    sentenceRanges(toString()).forEach { (s, e) ->
                                        if (caret !in s until e && e > s) {
                                            addStyle(SpanStyle(color = scheme.onSurface.copy(alpha = 0.35f)), s, e)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .onFocusChanged { if (it.isFocused) vm.focusField(key) }
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
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                                .onFocusChanged { if (it.isFocused) vm.focusField("h:${block.id.raw}") },
                        )
                    }
                    is Block.Quote -> Row(Modifier.padding(vertical = 4.dp)) {
                        Box(Modifier.padding(end = 12.dp).width(3.dp).heightIn(min = 24.dp).background(scheme.primary))
                        BasicTextField(
                            state = vm.fieldForQuote(block),
                            textStyle = bodyNoteStyle(size, fontFamily).copy(color = scheme.onSurfaceVariant),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier.weight(1f)
                                .onFocusChanged { if (it.isFocused) vm.focusField("q:${block.id.raw}") },
                        )
                    }
                    is Block.Divider -> HorizontalDivider(Modifier.padding(vertical = 16.dp), color = scheme.outlineVariant)
                    is Block.Checklist -> ChecklistEditor(
                        block = block,
                        sortChecked = sortChecked,
                        bodyStyle = bodyNoteStyle(size, fontFamily),
                        fieldFor = vm::fieldForCheck,
                        onToggle = { vm.toggleCheck(block.id.raw, it) },
                        onAdd = { vm.addCheckItem(block.id.raw) },
                        onAddAfter = { vm.addCheckItemAfter(block.id.raw, it) },
                        onRemove = { vm.removeCheckItem(block.id.raw, it) },
                        onFocus = { vm.focusField(it) },
                        onMove = { from, to -> vm.reorderCheck(block.id.raw, from, to) },
                    )
                    is Block.Bullets -> ListEditor(
                        items = block.items,
                        numbered = false,
                        bodyStyle = bodyNoteStyle(size, fontFamily),
                        fieldFor = { vm.fieldForBullet(block, it) },
                        onAdd = { vm.addListItem(block.id.raw) },
                        onAddAfter = { vm.addListItemAfter(block.id.raw, it) },
                        onRemove = { vm.removeListItem(block.id.raw, it) },
                        onFocus = { vm.focusField("b:${block.id.raw}:$it") },
                    )
                    is Block.Numbered -> ListEditor(
                        items = block.items,
                        numbered = true,
                        bodyStyle = bodyNoteStyle(size, fontFamily),
                        fieldFor = { vm.fieldForNumber(block, it) },
                        onAdd = { vm.addListItem(block.id.raw) },
                        onAddAfter = { vm.addListItemAfter(block.id.raw, it) },
                        onRemove = { vm.removeListItem(block.id.raw, it) },
                        onFocus = { vm.focusField("n:${block.id.raw}:$it") },
                    )
                    is Block.Code -> Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(scheme.surfaceContainer, MaterialTheme.shapes.large)
                            .padding(12.dp),
                    ) {
                        BasicTextField(
                            state = vm.fieldForCode(block),
                            textStyle = bodyNoteStyle(size, JetBrainsMono).copy(color = scheme.onSurface),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier.fillMaxWidth()
                                .onFocusChanged { if (it.isFocused) vm.focusField("d:${block.id.raw}") },
                        )
                    }
                    is Block.Table -> TableEditor(
                        block = block,
                        bodyStyle = bodyNoteStyle(size, fontFamily),
                        fieldFor = { vm.fieldForTable(block, it) },
                        onAddRow = { vm.addTableRow(block.id.raw) },
                        onAddCol = { vm.addTableCol(block.id.raw) },
                        onRemoveRow = { vm.removeTableRow(block.id.raw) },
                        onRemoveCol = { vm.removeTableCol(block.id.raw) },
                        onToggleHeader = { vm.toggleTableHeader(block.id.raw) },
                        onFocus = { vm.focusField("t:${block.id.raw}:$it") },
                    )
                    is Block.Image -> ImageEditor(
                        block = block,
                        filesDir = context.filesDir,
                        captionField = vm.fieldForCaption(block),
                        onView = { lightboxFile = File(context.filesDir, "media/${block.mediaId.raw}.jpg") },
                        onReplace = { replaceImageId = block.id.raw; imageSource = true },
                        onRemove = { vm.removeBlock(block.id.raw) },
                        onFocus = { vm.focusField("img:${block.id.raw}") },
                    )
                    is Block.Ink -> Column {
                        InkCanvas(
                            inkId = block.inkId.raw,
                            noteId = n.id.raw,
                            inkRepo = vm.inkRepo,
                            onPersist = { path -> vm.updateInk(block.id.raw, block.inkId.raw, path) },
                        )
                    }
                    }
                }
            }
        }

        FormatToolbar(
            onInsert = { insert = true },
            onStyle = { vm.toggleStyle(it) },
            onLink = { linkDialog = true },
            active = if (styleEpoch >= 0) vm.activeStyles() else emptySet(),
            hasSelection = focusedField?.startsWith("p:") == true,
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
                    "ink" -> Block.Ink(id, com.notesup.app.domain.model.InkId.random(), null)
                    "num" -> Block.Numbered(id, listOf(""))
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
                runCatching { camera.launch(uri) }.onFailure {
                    cameraError = context.getString(R.string.no_camera)
                }
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

    if (moveSheet) {
        com.notesup.app.ui.project.MoveToProjectSheet(
            projects = projects,
            onDismiss = { moveSheet = false },
            onPick = { id -> vm.moveToProject(id); moveSheet = false },
        )
    }
    lightboxFile?.let { file ->
        Dialog(onDismissRequest = { lightboxFile = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Lightbox(file, onClose = { lightboxFile = null })
        }
    }
    if (linkDialog) {
        var url by remember { mutableStateOf("https://") }
        AlertDialog(
            onDismissRequest = { linkDialog = false },
            title = { Text(stringResource(R.string.add_link)) },
            text = {
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text(stringResource(R.string.link_url)) })
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.toggleStyle(SpanStyleTag.LINK, url.trim())
                    linkDialog = false
                }) { Text(stringResource(R.string.add_link)) }
            },
            dismissButton = { TextButton(onClick = { linkDialog = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
    cameraError?.let { msg ->
        AlertDialog(
            onDismissRequest = { cameraError = null },
            title = { Text(stringResource(R.string.image_fail)) },
            text = { Text(msg) },
            confirmButton = { TextButton(onClick = { cameraError = null }) { Text(stringResource(R.string.done)) } },
        )
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
private fun FormatToolbar(
    onInsert: () -> Unit,
    onStyle: (SpanStyleTag) -> Unit,
    onLink: () -> Unit,
    active: Set<SpanStyleTag>,
    hasSelection: Boolean,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp)
            .background(scheme.surfaceContainerHigh, MaterialTheme.shapes.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (hasSelection) {
            StyleBtn(NotesupIcons.Bold, SpanStyleTag.BOLD in active) { onStyle(SpanStyleTag.BOLD) }
            StyleBtn(NotesupIcons.Italic, SpanStyleTag.ITALIC in active) { onStyle(SpanStyleTag.ITALIC) }
            StyleBtn(NotesupIcons.Underline, SpanStyleTag.UNDERLINE in active) { onStyle(SpanStyleTag.UNDERLINE) }
            StyleBtn(NotesupIcons.Strike, SpanStyleTag.STRIKE in active) { onStyle(SpanStyleTag.STRIKE) }
            StyleBtn(NotesupIcons.Code, SpanStyleTag.CODE in active) { onStyle(SpanStyleTag.CODE) }
            StyleBtn(NotesupIcons.Link, SpanStyleTag.LINK in active, onLink)
        }
        IconButton(onClick = onInsert) { NuIcon(NotesupIcons.Add, stringResource(R.string.insert)) }
    }
}

@Composable
private fun StyleBtn(icon: Int, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) { NuIcon(icon, null) }
    }
}

private fun com.notesup.app.domain.model.RichSpan.toComposeStyle(
    scheme: androidx.compose.material3.ColorScheme,
    fontFamily: androidx.compose.ui.text.font.FontFamily,
): SpanStyle = when (style) {
    SpanStyleTag.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
    SpanStyleTag.ITALIC -> SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    SpanStyleTag.UNDERLINE -> SpanStyle(textDecoration = TextDecoration.Underline)
    SpanStyleTag.STRIKE -> SpanStyle(textDecoration = TextDecoration.LineThrough)
    SpanStyleTag.CODE -> SpanStyle(fontFamily = JetBrainsMono, background = scheme.surfaceContainer)
    SpanStyleTag.LINK -> SpanStyle(color = scheme.primary, textDecoration = TextDecoration.Underline)
}

private fun sentenceRanges(text: String): List<Pair<Int, Int>> {
    if (text.isEmpty()) return emptyList()
    val out = mutableListOf<Pair<Int, Int>>()
    var start = 0
    text.forEachIndexed { i, c ->
        if (c == '.' || c == '!' || c == '?') {
            out.add(start to i + 1)
            start = i + 1
        }
    }
    if (start < text.length) out.add(start to text.length)
    return out
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
            "ink" to R.string.drawing,
            "image" to R.string.type_image,
            "num" to R.string.numbers,
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

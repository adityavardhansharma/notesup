package com.notesup.app.ui.editor

import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ink.authoring.compose.InProgressStrokes
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import com.notesup.app.R
import com.notesup.app.domain.model.InkId
import com.notesup.app.domain.model.NoteId
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.theme.TintWashes
import kotlinx.coroutines.launch

private enum class InkTool { Pen, Highlighter, Eraser }

@Composable
fun InkCanvas(
    inkId: String,
    noteId: String,
    heightDp: Float = 280f,
    inkRepo: com.notesup.app.data.repo.InkRepository,
    onPersist: (previewPath: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val committed = remember { mutableStateListOf<Stroke>() }
    val undone = remember { mutableStateListOf<Stroke>() }
    var tool by remember { mutableStateOf(InkTool.Pen) }
    var widthIndex by remember { mutableIntStateOf(1) }
    var colorIndex by remember { mutableIntStateOf(0) }
    var showWidth by remember { mutableStateOf(false) }
    var showPalette by remember { mutableStateOf(false) }
    val widths = listOf(2f, 4f, 8f, 14f)
    val palette = listOf(
        Color(0xFF1C1917),
        Color(0xFF8B2942),
        Color(0xFF765A00),
        Color(0xFF2F5E63),
        Color(0xFF3D4C7A),
        Color.White,
        TintWashes[3],
        TintWashes[6],
    )
    val renderer = remember { CanvasStrokeRenderer.create() }
    val scope = rememberCoroutineScope()
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(inkId) {
        val loaded = inkRepo.load(InkId(inkId))
        committed.clear()
        if (loaded != null) committed.addAll(loaded.first)
    }

    fun persist() {
        scope.launch {
            val path = inkRepo.save(InkId(inkId), NoteId(noteId), committed.toList(), heightDp)
            onPersist(path)
        }
    }

    val brush = remember(tool, widthIndex, colorIndex) {
        val family = when (tool) {
            InkTool.Highlighter -> StockBrushes.highlighter()
            else -> StockBrushes.pressurePen()
        }
        val color = if (tool == InkTool.Highlighter) {
            palette[colorIndex].copy(alpha = 0.4f).toArgb()
        } else {
            palette[colorIndex].toArgb()
        }
        val size = if (tool == InkTool.Highlighter) widths[widthIndex] * 2f else widths[widthIndex]
        Brush.createWithColorIntArgb(family, color, size, 0.1f)
    }

    Box(
        modifier
            .fillMaxWidth()
            .height(heightDp.dp)
            .clip(MaterialTheme.shapes.large)
            .background(scheme.surfaceContainerLow)
            .border(1.dp, scheme.outlineVariant, MaterialTheme.shapes.large),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val native = drawContext.canvas.nativeCanvas
            val matrix = Matrix()
            committed.forEach { renderer.draw(native, it, matrix) }
        }
        if (tool != InkTool.Eraser) {
            InProgressStrokes(
                defaultBrush = brush,
                onStrokesFinished = { fresh ->
                    committed.addAll(fresh)
                    undone.clear()
                    persist()
                },
            )
        } else {
            InProgressStrokes(
                defaultBrush = Brush.createWithColorIntArgb(StockBrushes.marker(), Color.Transparent.toArgb(), widths[widthIndex] * 3f, 0.1f),
                onStrokesFinished = { erasers ->
                    val hit = erasers.flatMap { eraser ->
                        committed.filter { existing -> strokesOverlap(existing, eraser) }
                    }
                    if (hit.isNotEmpty()) {
                        committed.removeAll(hit.toSet())
                        persist()
                    }
                },
            )
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(scheme.surfaceContainerHigh, MaterialTheme.shapes.extraLarge),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ToolBtn(NotesupIcons.Pen, stringResource(R.string.cd_pen), tool == InkTool.Pen) { tool = InkTool.Pen }
        ToolBtn(NotesupIcons.Highlighter, stringResource(R.string.cd_highlighter), tool == InkTool.Highlighter) { tool = InkTool.Highlighter }
        ToolBtn(NotesupIcons.Eraser, stringResource(R.string.cd_eraser), tool == InkTool.Eraser) { tool = InkTool.Eraser }
        ToolBtn(NotesupIcons.Width, stringResource(R.string.cd_thickness), showWidth) { showWidth = !showWidth; showPalette = false }
        ToolBtn(NotesupIcons.Palette, stringResource(R.string.cd_color), showPalette) { showPalette = !showPalette; showWidth = false }
        IconButton(onClick = {
            if (committed.isNotEmpty()) {
                undone.add(committed.removeAt(committed.lastIndex))
                persist()
            }
        }, enabled = committed.isNotEmpty()) { NuIcon(NotesupIcons.Undo, stringResource(R.string.cd_undo)) }
        IconButton(onClick = {
            if (undone.isNotEmpty()) {
                committed.add(undone.removeAt(undone.lastIndex))
                persist()
            }
        }, enabled = undone.isNotEmpty()) { NuIcon(NotesupIcons.Redo, stringResource(R.string.cd_redo)) }
    }
    if (showWidth) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            widths.forEachIndexed { i, w ->
                Box(
                    Modifier
                        .size((12 + w * 2).dp)
                        .clip(CircleShape)
                        .background(if (i == widthIndex) scheme.primary else scheme.onSurface)
                        .clickable { widthIndex = i; showWidth = false },
                )
            }
        }
    }
    if (showPalette) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            palette.forEachIndexed { i, c ->
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(c)
                        .border(if (i == colorIndex) 2.dp else 1.dp, if (i == colorIndex) scheme.primary else scheme.outline, CircleShape)
                        .clickable { colorIndex = i; showPalette = false },
                )
            }
        }
    }
}

@Composable
private fun ToolBtn(icon: Int, cd: String, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Box(
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            NuIcon(icon, cd)
        }
    }
}

private fun strokesOverlap(a: Stroke, b: Stroke): Boolean {
    val ptsA = sample(a)
    val ptsB = sample(b)
    if (ptsA.isEmpty() || ptsB.isEmpty()) return false
    for (p in ptsA) {
        for (q in ptsB) {
            val dx = p.first - q.first
            val dy = p.second - q.second
            if (dx * dx + dy * dy < 400f) return true
        }
    }
    return false
}

private fun sample(stroke: Stroke): List<Pair<Float, Float>> {
    val n = stroke.inputs.size
    if (n == 0) return emptyList()
    val step = (n / 12).coerceAtLeast(1)
    return buildList {
        var i = 0
        while (i < n) {
            val input = stroke.inputs[i]
            add(input.x to input.y)
            i += step
        }
    }
}

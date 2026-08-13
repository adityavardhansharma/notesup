package com.notesup.app.ui.common.expressive

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.notesup.app.ui.editor.FormatToolbarPlaceholder

@Composable
fun NotesupEditorToolbar(modifier: Modifier = Modifier, onInsert: () -> Unit = {}) {
    FormatToolbarPlaceholder(onInsert, modifier)
}

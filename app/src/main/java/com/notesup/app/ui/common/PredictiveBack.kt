package com.notesup.app.ui.common

import androidx.compose.runtime.Composable

@Composable
fun NotesupPredictiveBack(enabled: Boolean = true, onCommit: () -> Unit) {
    androidx.activity.compose.PredictiveBackHandler(enabled) { progress ->
        try {
            progress.collect { }
            onCommit()
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        }
    }
}

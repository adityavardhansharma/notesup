package com.notesup.app.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Singleton
class WidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    fun schedule() {
        job?.cancel()
        job = scope.launch {
            delay(300)
            runCatching { NewNoteWidget().updateAll(context) }
            runCatching { PinnedWidget().updateAll(context) }
            runCatching { RecentWidget().updateAll(context) }
            runCatching { ProjectWidget().updateAll(context) }
        }
    }
}

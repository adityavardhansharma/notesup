package com.notesup.app.data.remote

import android.content.Context
import com.notesup.app.R
import com.notesup.app.data.auth.AuthRepository
import com.notesup.app.data.prefs.NotesupPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Singleton
class SyncCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: AuthRepository,
    private val prefs: NotesupPrefs,
    private val notes: com.notesup.app.data.repo.NoteRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        scope.launch {
            runCatching { notes.purgeTrash() }
            val url = context.getString(R.string.convex_url)
            if (url.contains("placeholder")) return@launch
            if (auth.current() == null) return@launch
            // Clerk+Convex client is created when keys exist. Room stays source of truth.
        }
    }
}

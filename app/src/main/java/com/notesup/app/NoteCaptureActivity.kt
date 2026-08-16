package com.notesup.app

import android.app.KeyguardManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.ui.editor.EditorScreen
import com.notesup.app.ui.editor.EditorViewModel
import com.notesup.app.ui.theme.NotesupTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class NoteCaptureActivity : FragmentActivity() {
    @Inject lateinit var notes: NoteRepository
    @Inject lateinit var prefs: NotesupPrefs

    private var noteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        runBlocking { prefs.setOnboardingDone(true) }
        // Reuse the note created on first launch; recreating the activity (e.g. a
        // rotation) must not spawn a second blank note.
        val id = savedInstanceState?.getString(KEY_NOTE_ID) ?: run {
            val stylus = intent?.getBooleanExtra("android.intent.extra.USE_STYLUS_MODE", false) == true
            val kind = if (stylus) {
                NoteKind.INK
            } else {
                when (runBlocking { prefs.defaultKind.first() }) {
                    "checklist" -> NoteKind.CHECKLIST
                    "ink" -> NoteKind.INK
                    else -> NoteKind.TEXT
                }
            }
            runBlocking { notes.create(kind) }.id.raw
        }
        noteId = id
        setContent {
            val theme = androidx.compose.runtime.remember { runBlocking { prefs.theme.first() } }
            val appTheme = androidx.compose.runtime.remember { runBlocking { prefs.appTheme.first() } }
            NotesupTheme(themePref = theme, appTheme = appTheme) {
                val vm: EditorViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                LaunchedEffect(id) { vm.attach(id) }
                androidx.compose.foundation.layout.Column {
                    Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { finish() }) { Text(stringResource(R.string.done)) }
                    }
                    EditorScreen(
                        noteId = id,
                        vm = vm,
                        created = true,
                        onBack = { finish() },
                        onMissing = { finish() },
                    )
                }
            }
        }
        val kg = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (kg.isKeyguardLocked) {
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE,
                android.view.WindowManager.LayoutParams.FLAG_SECURE,
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        noteId?.let { outState.putString(KEY_NOTE_ID, it) }
    }

    private companion object {
        const val KEY_NOTE_ID = "noteId"
    }
}

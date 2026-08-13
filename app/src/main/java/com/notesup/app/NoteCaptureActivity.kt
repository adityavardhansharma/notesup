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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        runBlocking { prefs.setOnboardingDone(true) }
        val stylus = intent?.getBooleanExtra("android.intent.extra.USE_STYLUS_MODE", false) == true
        val kind = if (stylus) NoteKind.INK else NoteKind.TEXT
        val note = runBlocking { notes.create(kind) }
        setContent {
            val theme = runBlocking { prefs.theme.first() }
            NotesupTheme(themePref = theme) {
                val vm: EditorViewModel = androidx.hilt.navigation.compose.hiltViewModel()
                LaunchedEffect(note.id.raw) { vm.attach(note.id.raw) }
                androidx.compose.foundation.layout.Column {
                    Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { finish() }) { Text(stringResource(R.string.done)) }
                    }
                    EditorScreen(
                        noteId = note.id.raw,
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
}

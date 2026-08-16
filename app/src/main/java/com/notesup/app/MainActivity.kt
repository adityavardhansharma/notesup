package com.notesup.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.MediaId
import com.notesup.app.domain.model.NoteKind
import com.notesup.app.domain.model.RichText
import com.notesup.app.ui.navigation.NotesupNav
import com.notesup.app.ui.theme.NotesupTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject lateinit var prefs: NotesupPrefs
    @Inject lateinit var notes: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { false }
        enableEdgeToEdge()
        handleShare(intent)
        setContent {
            // Read persisted prefs once; a bare runBlocking here would re-block the
            // main thread on every recomposition of the root.
            val theme = androidx.compose.runtime.remember { runBlocking { prefs.theme.first() } }
            val appTheme = androidx.compose.runtime.remember { runBlocking { prefs.appTheme.first() } }
            NotesupTheme(themePref = theme, appTheme = appTheme) {
                NotesupNav(prefs = prefs, initialDeepLink = intent?.dataString)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleShare(intent)
    }

    private fun handleShare(intent: Intent?) {
        if (intent == null) return
        val action = intent.action ?: return
        if (action != Intent.ACTION_SEND && action != Intent.ACTION_SEND_MULTIPLE) return
        runBlocking { prefs.setOnboardingDone(true) }
        val text = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
        val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT).orEmpty()
        runBlocking {
            val body = listOf(subject, text).filter { it.isNotBlank() }.joinToString("\n\n")
            notes.create(
                kind = NoteKind.TEXT,
                extraBlocks = if (body.isBlank()) emptyList()
                else listOf(Block.Paragraph(com.notesup.app.domain.model.BlockId.random(), RichText.of(body))),
            )
        }
    }
}

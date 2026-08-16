package com.notesup.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.repo.MediaRepository
import com.notesup.app.data.repo.NoteRepository
import com.notesup.app.domain.model.Block
import com.notesup.app.domain.model.BlockId
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
    @Inject lateinit var media: MediaRepository

    private val launchLink = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        splash.setKeepOnScreenCondition { false }
        enableEdgeToEdge()
        if (savedInstanceState == null) {
            launchLink.value = resolveLaunch(intent)
        }
        setContent {
            val theme by prefs.theme.collectAsStateWithLifecycle(initialValue = "system")
            val appTheme by prefs.appTheme.collectAsStateWithLifecycle(initialValue = "dynamic")
            NotesupTheme(themePref = theme, appTheme = appTheme) {
                NotesupNav(prefs = prefs, initialDeepLink = launchLink.value)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchLink.value = resolveLaunch(intent)
    }

    private fun resolveLaunch(intent: Intent?): String? {
        if (intent == null) return null
        val shared = handleShare(intent)
        if (shared != null) return "notesup://note/$shared"
        val data = intent.data ?: return intent.dataString
        if (data.scheme != "notesup") return intent.dataString
        if (data.host == "new") {
            val kind = when (data.getQueryParameter("kind")) {
                "checklist" -> NoteKind.CHECKLIST
                "ink" -> NoteKind.INK
                else -> NoteKind.TEXT
            }
            val created = runBlocking { notes.create(kind) }
            return "notesup://note/${created.id.raw}"
        }
        return data.toString()
    }

    private fun handleShare(intent: Intent): String? {
        val action = intent.action ?: return null
        if (action != Intent.ACTION_SEND && action != Intent.ACTION_SEND_MULTIPLE) return null
        runBlocking { prefs.setOnboardingDone(true) }
        val text = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
        val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT).orEmpty()
        val body = listOf(subject, text).filter { it.isNotBlank() }.joinToString("\n\n")
        val streams = shareUris(intent, action)
        if (body.isBlank() && streams.isEmpty()) return null
        return runBlocking {
            val blocks = mutableListOf<Block>()
            if (body.isNotBlank()) {
                blocks += Block.Paragraph(BlockId.random(), RichText.of(body))
            }
            val note = notes.create(NoteKind.TEXT, extraBlocks = blocks.ifEmpty { emptyList() })
            val imported = streams.mapNotNull { uri ->
                runCatching { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
                media.import(note.id, uri)?.let { Block.Image(BlockId.random(), MediaId(it.id), "") }
            }
            if (imported.isNotEmpty()) {
                val current = notes.get(note.id) ?: note
                notes.save(current.copy(blocks = current.blocks + imported))
            }
            Toast.makeText(this@MainActivity, getString(R.string.saved_to_notesup), Toast.LENGTH_SHORT).show()
            note.id.raw
        }
    }

    @Suppress("DEPRECATION")
    private fun shareUris(intent: Intent, action: String): List<Uri> {
        return if (action == Intent.ACTION_SEND_MULTIPLE) {
            if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java).orEmpty()
            } else {
                intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM).orEmpty()
            }
        } else {
            val one = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            }
            listOfNotNull(one)
        }
    }
}

package com.notesup.app.widget

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.action.clickable
import androidx.glance.layout.Box
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.notesup.app.R
import com.notesup.app.data.local.toDomain
import com.notesup.app.domain.model.Note
import com.notesup.app.ui.home.relative
import com.notesup.app.ui.navigation.notesupUri
import com.notesup.app.ui.navigation.notesupViewIntent
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

private val Paper = Color(0xFFF6F1EA)
private val Wax = Color(0xFF8B2942)
private val Ink = Color(0xFF1C1917)

private fun entry(context: Context): WidgetEntryPoint =
    EntryPointAccessors.fromApplication(context.applicationContext, WidgetEntryPoint::class.java)

internal suspend fun widgetNotes(context: Context): List<Note> =
    entry(context).noteDao().observeAlive().first().map { it.toDomain() }

private fun noteIntent(context: Context, id: String) =
    notesupViewIntent(context, notesupUri("note/$id"))

private fun newIntent(context: Context, kind: String) =
    notesupViewIntent(context, notesupUri("new").buildUpon().appendQueryParameter("kind", kind).build())

class NewNoteWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Row(
                    GlanceModifier.fillMaxSize().background(ColorProvider(Paper)).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Cell(context.getString(R.string.shortcut_note), true, newIntent(context, "text"))
                    Cell(context.getString(R.string.shortcut_list), false, newIntent(context, "checklist"))
                    Cell(context.getString(R.string.shortcut_ink), false, newIntent(context, "ink"))
                }
            }
        }
    }

    @Composable
    private fun Cell(label: String, wax: Boolean, intent: android.content.Intent) {
        Box(
            GlanceModifier.fillMaxHeight().padding(4.dp)
                .background(ColorProvider(if (wax) Wax else Paper))
                .clickable(actionStartActivity(intent)),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, style = TextStyle(color = ColorProvider(if (wax) Color.White else Ink)))
        }
    }
}

class PinnedWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val notes = widgetNotes(context).filter { it.pinned }.take(5)
        provideContent {
            NoteListWidget(
                title = context.getString(R.string.filter_pinned),
                notes = notes,
                empty = context.getString(R.string.nothing_pinned_yet),
            )
        }
    }
}

class RecentWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val notes = widgetNotes(context).sortedByDescending { it.updatedAt }.take(5)
        provideContent {
            NoteListWidget(
                title = context.getString(R.string.filter_recent),
                notes = notes,
                empty = context.getString(R.string.empty_home),
            )
        }
    }
}

class ProjectWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = runCatching { GlanceAppWidgetManager(context).getAppWidgetId(id) }.getOrDefault(-1)
        val projectId = if (appWidgetId >= 0) entry(context).prefs().widgetProjectNow(appWidgetId) else null
        val all = widgetNotes(context)
        val notes = all.filter { if (projectId == null) it.projectId == null else it.projectId?.raw == projectId }.take(5)
        val header = if (projectId == null) {
            context.getString(R.string.inbox)
        } else {
            entry(context).projectDao().get(projectId)?.name ?: context.getString(R.string.inbox)
        }
        provideContent {
            NoteListWidget(title = header, notes = notes, empty = context.getString(R.string.empty_home))
        }
    }
}

@Composable
private fun NoteListWidget(title: String, notes: List<Note>, empty: String) {
    val context = LocalContext.current
    Column(
        GlanceModifier.fillMaxSize().background(ColorProvider(Paper)).padding(12.dp),
    ) {
        Row(GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorProvider(Ink)))
            Spacer(GlanceModifier.width(8.dp))
            Text(
                context.getString(R.string.widget_wordmark),
                style = TextStyle(fontSize = 11.sp, color = ColorProvider(Ink.copy(alpha = 0.6f))),
            )
        }
        if (notes.isEmpty()) {
            Spacer(GlanceModifier.height(16.dp))
            Text(empty, style = TextStyle(color = ColorProvider(Color(0xFF52443F)), fontSize = 14.sp))
        } else {
            notes.forEachIndexed { i, note ->
                if (i > 0) {
                    Spacer(GlanceModifier.height(1.dp).fillMaxWidth().background(ColorProvider(Color(0xFFD6C3BC).copy(alpha = 0.4f))))
                }
                Row(
                    GlanceModifier.fillMaxWidth().height(44.dp).clickable(actionStartActivity(noteIntent(context, note.id.raw))),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        note.displayTitle,
                        style = TextStyle(color = ColorProvider(Ink), fontSize = 14.sp),
                        maxLines = 1,
                    )
                    Text(relative(note.updatedAt), style = TextStyle(color = ColorProvider(Color(0xFF52443F)), fontSize = 11.sp))
                }
            }
        }
    }
}

class NewNoteWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = NewNoteWidget()
}

class PinnedWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = PinnedWidget()
}

class RecentWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = RecentWidget()
}

class ProjectWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = ProjectWidget()
}

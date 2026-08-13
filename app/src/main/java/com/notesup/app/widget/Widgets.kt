package com.notesup.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.notesup.app.MainActivity
import com.notesup.app.R

class NewNoteWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Row(
                    GlanceModifier.fillMaxSize().background(ColorProvider(Color(0xFFF6F1EA))).clickable(actionStartActivity<MainActivity>()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Cell(context.getString(R.string.shortcut_note), true)
                    Cell(context.getString(R.string.shortcut_list), false)
                    Cell(context.getString(R.string.shortcut_ink), false)
                }
            }
        }
    }

    @Composable
    private fun Cell(label: String, wax: Boolean) {
        androidx.glance.layout.Box(
            GlanceModifier.fillMaxWidth().height(48.dp).padding(4.dp)
                .background(ColorProvider(if (wax) Color(0xFF8B2942) else Color(0xFFF6F1EA))),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, style = TextStyle(color = ColorProvider(if (wax) Color.White else Color(0xFF1C1917))))
        }
    }
}

class PinnedWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                GlanceModifier.fillMaxSize().background(ColorProvider(Color(0xFFF6F1EA))).padding(12.dp)
                    .clickable(actionStartActivity<MainActivity>()),
            ) {
                Text(context.getString(R.string.pin_widget_empty))
            }
        }
    }
}

class RecentWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                GlanceModifier.fillMaxSize().background(ColorProvider(Color(0xFFF6F1EA))).padding(12.dp)
                    .clickable(actionStartActivity<MainActivity>()),
            ) {
                Text(context.getString(R.string.filter_recent))
                Spacer(GlanceModifier.height(8.dp))
                Text(context.getString(R.string.empty_home))
            }
        }
    }
}

class ProjectWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                GlanceModifier.fillMaxSize().background(ColorProvider(Color(0xFFF6F1EA))).padding(12.dp)
                    .clickable(actionStartActivity<MainActivity>()),
            ) {
                Text(context.getString(R.string.inbox))
                Text(context.getString(R.string.empty_home))
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

package com.notesup.app.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesup.app.R
import com.notesup.app.data.repo.ProjectRepository
import com.notesup.app.ui.theme.NotesupTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProjectWidgetConfigureActivity : FragmentActivity() {
    @Inject lateinit var projects: ProjectRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        setContent {
            NotesupTheme {
                val list by projects.observe().collectAsStateWithLifecycle(emptyList())
                LazyColumn {
                    item {
                        Text(
                            stringResource(R.string.inbox),
                            modifier = Modifier.fillMaxWidth().clickable { finishOk(appWidgetId, null) }.padding(20.dp),
                        )
                    }
                    items(list) { p ->
                        Text(
                            p.name,
                            modifier = Modifier.fillMaxWidth().clickable { finishOk(appWidgetId, p.id.raw) }.padding(20.dp),
                        )
                    }
                }
            }
        }
    }

    private fun finishOk(id: Int, projectId: String?) {
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}

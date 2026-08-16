package com.notesup.app.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.notesup.app.R
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.data.remote.SyncCoordinator
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncChip(state: HomeState, vm: SyncChipViewModel = hiltViewModel()) {
    var sheet by remember { mutableStateOf(false) }
    val paused by vm.prefs.syncPaused.collectAsStateWithLifecycle(false)
    val last by vm.prefs.lastSyncedAt.collectAsStateWithLifecycle(null)
    val spinning = rememberInfiniteTransition(label = "sync")
    val angle by spinning.animateFloat(
        0f,
        360f,
        infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "sync-rot",
    )
    val icon = if (paused || !state.signedIn) NotesupIcons.Offline else NotesupIcons.Sync
    val tint = if (paused || !state.signedIn) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
    val cd = if (paused) R.string.sync_paused else if (!state.signedIn) R.string.offline else R.string.cd_sync
    val spin = state.signedIn && !paused
    IconButton(onClick = { sheet = true }) {
        NuIcon(
            icon,
            stringResource(cd),
            Modifier.size(22.dp).then(if (spin) Modifier.rotate(angle) else Modifier),
            tint = tint,
        )
    }
    if (sheet) {
        ModalBottomSheet(onDismissRequest = { sheet = false }) {
            Column(Modifier.padding(20.dp).padding(bottom = 24.dp)) {
                Text(
                    last?.let { java.time.Instant.ofEpochMilli(it).toString() } ?: stringResource(R.string.last_synced_never),
                    style = MaterialTheme.typography.bodyMedium,
                )
                TextButton(onClick = { vm.syncNow(); sheet = false }) { Text(stringResource(R.string.sync_now)) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.pause_sync), modifier = Modifier.weight(1f))
                    Switch(paused, { vm.setPaused(it) })
                }
            }
        }
    }
}

@HiltViewModel
class SyncChipViewModel @Inject constructor(
    val prefs: NotesupPrefs,
    private val sync: SyncCoordinator,
) : ViewModel() {
    fun setPaused(value: Boolean) {
        viewModelScope.launch { prefs.setSyncPaused(value) }
    }

    fun syncNow() {
        sync.start()
    }
}

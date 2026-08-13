package com.notesup.app.ui.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesup.app.BuildConfig
import com.notesup.app.R
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.ui.common.EmptySentence
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel

@HiltViewModel
class SettingsViewModel @Inject constructor(val prefs: NotesupPrefs) : ViewModel()

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onAppearance: () -> Unit,
    onType: () -> Unit,
    onPaper: () -> Unit,
    onFocus: () -> Unit,
    onTrash: () -> Unit,
    onAccount: () -> Unit,
    onPrivacy: () -> Unit,
    onAbout: () -> Unit,
    vm: SettingsViewModel = hiltViewModel(),
) {
    val view by vm.prefs.homeView.collectAsStateWithLifecycle("grid")
    val sort by vm.prefs.sortChecked.collectAsStateWithLifecycle(false)
    val lockNew by vm.prefs.lockNew.collectAsStateWithLifecycle(false)
    val lockHist by vm.prefs.lockScreenHistory.collectAsStateWithLifecycle(false)
    val focus by vm.prefs.focus.collectAsStateWithLifecycle("auto")
    val paper by vm.prefs.defaultPaper.collectAsStateWithLifecycle("blank")
    val font by vm.prefs.defaultFont.collectAsStateWithLifecycle("roboto_flex")
    val theme by vm.prefs.appTheme.collectAsStateWithLifecycle("dynamic")

    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
        Bar(onBack, stringResource(R.string.settings))
        RowItem(stringResource(R.string.appearance), theme, onAppearance)
        RowItem(stringResource(R.string.type), font, onType)
        RowItem(stringResource(R.string.paper), paper, onPaper)
        ListItem(
            headlineContent = { Text(stringResource(R.string.home_view)) },
            trailingContent = { Text(if (view == "list") stringResource(R.string.list) else stringResource(R.string.grid)) },
            modifier = Modifier.clickable {
                // toggle via prefs through view model later
            },
        )
        RowItem(stringResource(R.string.focus), focus, onFocus)
        ListItem(
            headlineContent = { Text(stringResource(R.string.sort_checked)) },
            trailingContent = { Switch(sort, { /* */ }) },
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text(stringResource(R.string.lock_new)) },
            trailingContent = { Switch(lockNew, { }) },
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.lock_screen_history)) },
            trailingContent = { Switch(lockHist, { }) },
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.default_notes)) },
            supportingContent = { Text(stringResource(R.string.default_notes_body)) },
            trailingContent = { Text(stringResource(R.string.off)) },
        )
        HorizontalDivider()
        RowItem(stringResource(R.string.trash), "", onTrash)
        HorizontalDivider()
        RowItem(stringResource(R.string.account), "", onAccount)
        RowItem(stringResource(R.string.what_syncs), "", onPrivacy)
        RowItem(stringResource(R.string.about), BuildConfig.VERSION_NAME, onAbout)
    }
}

@Composable
fun AppearanceScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Bar(onBack, stringResource(R.string.appearance))
        Text(stringResource(R.string.wallpaper), modifier = Modifier.padding(20.dp))
    }
}

@Composable
fun TypeSettingsScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Bar(onBack, stringResource(R.string.type))
        Text("Roboto Flex", modifier = Modifier.padding(20.dp))
    }
}

@Composable
fun PaperSettingsScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Bar(onBack, stringResource(R.string.paper))
        Text("blank", modifier = Modifier.padding(20.dp))
    }
}

@Composable
fun FocusSettingsScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Bar(onBack, stringResource(R.string.focus))
        listOf(
            "off" to R.string.focus_off,
            "auto" to R.string.focus_auto,
            "sentence" to R.string.focus_sentence,
            "typewriter" to R.string.focus_typewriter,
        ).forEach { (_, res) ->
            Text(stringResource(res), modifier = Modifier.padding(20.dp, 12.dp))
        }
    }
}

@Composable
fun TrashScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Bar(onBack, stringResource(R.string.trash))
        EmptySentence(stringResource(R.string.nothing_trash))
    }
}

@Composable
fun ManageAccountScreen(onBack: () -> Unit, onSignIn: () -> Unit, onSignOut: () -> Unit) {
    var confirm by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Bar(onBack, stringResource(R.string.account))
        TextButtonRow(stringResource(R.string.sign_in), onSignIn)
        TextButtonRow(stringResource(R.string.sign_out)) { confirm = true }
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text(stringResource(R.string.sign_out_q)) },
            text = { Text(stringResource(R.string.sign_out_body)) },
            confirmButton = { TextButton(onClick = { confirm = false; onSignOut() }) { Text(stringResource(R.string.sign_out)) } },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Bar(onBack, stringResource(R.string.what_syncs))
        Text(stringResource(R.string.privacy_phone), style = MaterialTheme.typography.titleSmall)
        Text(stringResource(R.string.privacy_phone_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.privacy_in), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp))
        Text(stringResource(R.string.privacy_in_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.privacy_out), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp))
        Text(stringResource(R.string.privacy_out_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.privacy_backup), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp))
        Text(stringResource(R.string.privacy_backup_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.privacy_delete), style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp))
        Text(stringResource(R.string.privacy_delete_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().statusBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        Bar(onBack, stringResource(R.string.about))
        Text("Notesup", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 96.dp))
        Text("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSheet(onDismiss: () -> Unit, onSignIn: () -> Unit, onSettings: () -> Unit, onAbout: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(20.dp).padding(bottom = 24.dp)) {
            NuIcon(NotesupIcons.Account, null, Modifier.height(64.dp).fillMaxWidth())
            Text(stringResource(R.string.sign_in_to_sync), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.sign_in_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButtonRow(stringResource(R.string.sign_in), onSignIn)
            TextButtonRow(stringResource(R.string.settings), onSettings)
            TextButtonRow(stringResource(R.string.about), onAbout)
        }
    }
}

@Composable
private fun Bar(onBack: () -> Unit, title: String) {
    Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back)) }
        Text(title, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun RowItem(title: String, value: String, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = { if (value.isNotBlank()) Text(value, style = MaterialTheme.typography.labelSmall) },
        modifier = Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun TextButtonRow(title: String, onClick: () -> Unit) {
    Text(title, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(20.dp, 16.dp), style = MaterialTheme.typography.bodyLarge)
}

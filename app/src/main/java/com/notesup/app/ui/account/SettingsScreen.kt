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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.notesup.app.ui.home.NoteListRow
import com.notesup.app.ui.theme.AppThemeOptions
import com.notesup.app.ui.theme.Atkinson
import com.notesup.app.ui.theme.JetBrainsMono
import com.notesup.app.ui.theme.Literata
import com.notesup.app.ui.theme.bodyNoteStyle
import com.notesup.app.ui.theme.flex
import com.notesup.app.ui.theme.paperBackground
import java.time.Duration
import java.time.Instant

@HiltViewModel
class SettingsViewModel @Inject constructor(val prefs: NotesupPrefs) : ViewModel() {
    fun setHomeView(value: String) = viewModelScope.launch { prefs.setHomeView(value) }
    fun setSortChecked(value: Boolean) = viewModelScope.launch { prefs.setSortChecked(value) }
    fun setLockNew(value: Boolean) = viewModelScope.launch { prefs.setLockNew(value) }
    fun setLockScreenHistory(value: Boolean) = viewModelScope.launch { prefs.setLockScreenHistory(value) }
    fun setAppTheme(value: String) = viewModelScope.launch { prefs.setAppTheme(value) }
    fun setTheme(value: String) = viewModelScope.launch { prefs.setTheme(value) }
    fun setDefaultFont(value: String) = viewModelScope.launch { prefs.setDefaultFont(value) }
    fun setBodySize(value: String) = viewModelScope.launch { prefs.setBodySize(value) }
    fun setDefaultPaper(value: String) = viewModelScope.launch { prefs.setDefaultPaper(value) }
    fun setFocus(value: String) = viewModelScope.launch { prefs.setFocus(value) }
    fun setDefaultKind(value: String) = viewModelScope.launch { prefs.setDefaultKind(value) }
}

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val notes: com.notesup.app.data.repo.NoteRepository,
) : ViewModel() {
    val trash = notes.observeTrash().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    fun restore(id: String) = viewModelScope.launch {
        notes.setDeleted(com.notesup.app.domain.model.NoteId(id), false)
    }
    fun deleteForever(id: String) = viewModelScope.launch {
        notes.hardDelete(com.notesup.app.domain.model.NoteId(id))
    }
    fun empty() = viewModelScope.launch { notes.emptyTrash() }
}

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val auth: com.notesup.app.data.auth.AuthRepository,
) : ViewModel() {
    val signedIn = auth.signedIn
    val user = auth.user
    fun signOut(onDone: () -> Unit) = viewModelScope.launch {
        auth.signOut()
        onDone()
    }
    fun deleteAccount(onDone: () -> Unit) = viewModelScope.launch {
        auth.deleteAccount()
        onDone()
    }
}

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
    val kind by vm.prefs.defaultKind.collectAsStateWithLifecycle("text")
    var kindPicker by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
        Bar(onBack, stringResource(R.string.settings))
        RowItem(stringResource(R.string.appearance), theme, onAppearance)
        RowItem(stringResource(R.string.type), font, onType)
        RowItem(stringResource(R.string.paper), paper, onPaper)
        ListItem(
            headlineContent = { Text(stringResource(R.string.home_view)) },
            trailingContent = { Text(if (view == "list") stringResource(R.string.list) else stringResource(R.string.grid)) },
            modifier = Modifier.clickable {
                vm.setHomeView(if (view == "list") "grid" else "list")
            },
        )
        RowItem(stringResource(R.string.focus), focus, onFocus)
        ListItem(
            headlineContent = { Text(stringResource(R.string.sort_checked)) },
            trailingContent = { Switch(sort, { vm.setSortChecked(it) }) },
        )
        HorizontalDivider()
        ListItem(
            headlineContent = { Text(stringResource(R.string.lock_new)) },
            trailingContent = { Switch(lockNew, { vm.setLockNew(it) }) },
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.lock_screen_history)) },
            trailingContent = { Switch(lockHist, { vm.setLockScreenHistory(it) }) },
        )
        ListItem(
            headlineContent = { Text(stringResource(R.string.default_kind)) },
            supportingContent = { Text(stringResource(R.string.default_notes_body)) },
            trailingContent = {
                Text(
                    when (kind) {
                        "checklist" -> stringResource(R.string.checklist)
                        "ink" -> stringResource(R.string.drawing)
                        else -> stringResource(R.string.text)
                    },
                )
            },
            modifier = Modifier.clickable { kindPicker = true },
        )
        if (kindPicker) {
            AlertDialog(
                onDismissRequest = { kindPicker = false },
                title = { Text(stringResource(R.string.default_kind)) },
                text = {
                    Column {
                        listOf(
                            "text" to R.string.text,
                            "checklist" to R.string.checklist,
                            "ink" to R.string.drawing,
                        ).forEach { (key, res) ->
                            ListItem(
                                headlineContent = { Text(stringResource(res)) },
                                trailingContent = { if (kind == key) NuIcon(NotesupIcons.Check, null) },
                                modifier = Modifier.clickable {
                                    vm.setDefaultKind(key)
                                    kindPicker = false
                                },
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { kindPicker = false }) { Text(stringResource(R.string.done)) }
                },
            )
        }
        HorizontalDivider()
        RowItem(stringResource(R.string.trash), "", onTrash)
        HorizontalDivider()
        RowItem(stringResource(R.string.account), "", onAccount)
        RowItem(stringResource(R.string.what_syncs), "", onPrivacy)
        RowItem(stringResource(R.string.about), BuildConfig.VERSION_NAME, onAbout)
    }
}

@Composable
fun AppearanceScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val appTheme by vm.prefs.appTheme.collectAsStateWithLifecycle("dynamic")
    val theme by vm.prefs.theme.collectAsStateWithLifecycle("system")
    val haptics = com.notesup.app.ui.common.rememberHaptics()
    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
        Bar(onBack, stringResource(R.string.appearance))
        Text(
            stringResource(R.string.app_theme),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        AppThemeOptions.forEach { opt ->
            if (opt.key == "dynamic" && android.os.Build.VERSION.SDK_INT < 31) return@forEach
            ListItem(
                headlineContent = { Text(stringResource(opt.labelRes)) },
                leadingContent = {
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(opt.swatchSurface),
                    ) {
                        Box(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .size(14.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp))
                                .background(opt.swatchPrimary),
                        )
                    }
                },
                trailingContent = { if (appTheme == opt.key) NuIcon(NotesupIcons.Check, null) },
                modifier = Modifier.clickable {
                    haptics.tick()
                    vm.setAppTheme(opt.key)
                },
            )
        }
        HorizontalDivider()
        Text(
            stringResource(R.string.light_dark),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        Row(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            listOf("system" to R.string.theme_system, "light" to R.string.theme_light, "dark" to R.string.theme_dark).forEach { (key, res) ->
                val selected = theme == key
                Text(
                    stringResource(res),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { haptics.tick(); vm.setTheme(key) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
fun TypeSettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val font by vm.prefs.defaultFont.collectAsStateWithLifecycle("roboto_flex")
    val size by vm.prefs.bodySize.collectAsStateWithLifecycle("M")
    val haptics = com.notesup.app.ui.common.rememberHaptics()
    val family = when (font) {
        "literata" -> Literata
        "jetbrains_mono" -> JetBrainsMono
        "atkinson" -> Atkinson
        else -> flex(400, 18f)
    }
    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
        Bar(onBack, stringResource(R.string.type))
        Text(
            "The line you write sits on the page like this.",
            style = bodyNoteStyle(size, family),
            modifier = Modifier.padding(20.dp),
        )
        Text(
            stringResource(R.string.default_font),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        listOf(
            "roboto_flex" to R.string.font_roboto to flex(400, 16f),
            "literata" to R.string.font_literata to Literata,
            "jetbrains_mono" to R.string.font_mono to JetBrainsMono,
            "atkinson" to R.string.font_atkinson to Atkinson,
        ).forEach { (pair, fam) ->
            val (key, res) = pair
            ListItem(
                headlineContent = { Text(stringResource(res), fontFamily = fam) },
                trailingContent = { if (font == key) NuIcon(NotesupIcons.Check, null) },
                modifier = Modifier.clickable { haptics.tick(); vm.setDefaultFont(key) },
            )
        }
        HorizontalDivider()
        Text(
            stringResource(R.string.body_size),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )
        Row(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            listOf("S" to R.string.size_s, "M" to R.string.size_m, "L" to R.string.size_l).forEach { (key, res) ->
                val selected = size == key
                Text(
                    stringResource(res),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { haptics.tick(); vm.setBodySize(key) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
fun PaperSettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val paper by vm.prefs.defaultPaper.collectAsStateWithLifecycle("blank")
    val haptics = com.notesup.app.ui.common.rememberHaptics()
    val rule = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
        Bar(onBack, stringResource(R.string.paper))
        listOf(
            "blank" to R.string.paper_blank,
            "lines" to R.string.paper_lines,
            "dots" to R.string.paper_dots,
            "grid" to R.string.paper_grid,
        ).forEach { (key, res) ->
            ListItem(
                headlineContent = { Text(stringResource(res)) },
                leadingContent = {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .paperBackground(key, rule),
                    )
                },
                trailingContent = { if (paper == key) NuIcon(NotesupIcons.Check, null) },
                modifier = Modifier.clickable { haptics.tick(); vm.setDefaultPaper(key) },
            )
        }
    }
}

@Composable
fun FocusSettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = hiltViewModel()) {
    val focus by vm.prefs.focus.collectAsStateWithLifecycle("auto")
    val haptics = com.notesup.app.ui.common.rememberHaptics()
    Column(Modifier.fillMaxSize().statusBarsPadding().verticalScroll(rememberScrollState())) {
        Bar(onBack, stringResource(R.string.focus))
        listOf(
            "off" to R.string.focus_off to R.string.focus_off_body,
            "auto" to R.string.focus_auto to R.string.focus_auto_body,
            "sentence" to R.string.focus_sentence to R.string.focus_sentence_body,
            "typewriter" to R.string.focus_typewriter to R.string.focus_typewriter_body,
        ).forEach { (pair, body) ->
            val (key, title) = pair
            ListItem(
                headlineContent = { Text(stringResource(title)) },
                supportingContent = { Text(stringResource(body)) },
                trailingContent = { if (focus == key) NuIcon(NotesupIcons.Check, null) },
                modifier = Modifier.clickable { haptics.tick(); vm.setFocus(key) },
            )
        }
    }
}

@Composable
fun TrashScreen(onBack: () -> Unit, vm: TrashViewModel = hiltViewModel()) {
    val items by vm.trash.collectAsStateWithLifecycle()
    var confirmEmpty by remember { mutableStateOf(false) }
    var foreverId by remember { mutableStateOf<String?>(null) }
    var menuFor by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back)) }
            Text(stringResource(R.string.trash), style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = { confirmEmpty = true }, enabled = items.isNotEmpty()) {
                Text(stringResource(R.string.empty_trash))
            }
        }
        if (items.isEmpty()) {
            EmptySentence(stringResource(R.string.nothing_trash))
        } else {
            androidx.compose.foundation.lazy.LazyColumn(Modifier.fillMaxSize()) {
                items(items, key = { it.id.raw }) { n ->
                    Box {
                        NoteListRow(
                            note = n,
                            onClick = { menuFor = n.id.raw },
                            onLongClick = { menuFor = n.id.raw },
                            supporting = trashCountdown(n.deletedAt),
                        )
                        DropdownMenu(expanded = menuFor == n.id.raw, onDismissRequest = { menuFor = null }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.restore)) },
                                leadingIcon = { NuIcon(NotesupIcons.Undo, null) },
                                onClick = { vm.restore(n.id.raw); menuFor = null },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete_forever)) },
                                leadingIcon = { NuIcon(NotesupIcons.Delete, null) },
                                onClick = { foreverId = n.id.raw; menuFor = null },
                            )
                        }
                    }
                }
            }
        }
    }
    if (confirmEmpty) {
        AlertDialog(
            onDismissRequest = { confirmEmpty = false },
            title = { Text(stringResource(R.string.empty_trash_q)) },
            text = { Text(stringResource(R.string.empty_trash_body)) },
            confirmButton = {
                TextButton(onClick = { confirmEmpty = false; vm.empty() }) { Text(stringResource(R.string.empty_trash_action)) }
            },
            dismissButton = { TextButton(onClick = { confirmEmpty = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
    foreverId?.let { id ->
        AlertDialog(
            onDismissRequest = { foreverId = null },
            title = { Text(stringResource(R.string.delete_forever_q)) },
            text = { Text(stringResource(R.string.delete_forever_body)) },
            confirmButton = {
                TextButton(onClick = { vm.deleteForever(id); foreverId = null }) { Text(stringResource(R.string.delete_forever)) }
            },
            dismissButton = { TextButton(onClick = { foreverId = null }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

@Composable
private fun trashCountdown(deletedAt: Instant?): String {
    if (deletedAt == null) return ""
    val expires = deletedAt.plusSeconds(30L * 24 * 60 * 60)
    val left = Duration.between(Instant.now(), expires).toDays().toInt()
    val deletedToday = Duration.between(deletedAt, Instant.now()).toDays() < 1
    return if (deletedToday) stringResource(R.string.deleted_today)
    else stringResource(R.string.deleted_days, left.coerceAtLeast(0))
}

@Composable
fun ManageAccountScreen(
    onBack: () -> Unit,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    vm: AccountViewModel = hiltViewModel(),
) {
    var confirm by remember { mutableStateOf(false) }
    var deleteConfirm by remember { mutableStateOf(false) }
    var deleteTyped by remember { mutableStateOf("") }
    val signedIn by vm.signedIn.collectAsStateWithLifecycle(false)
    val user by vm.user.collectAsStateWithLifecycle(null)
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Bar(onBack, stringResource(R.string.account))
        if (signedIn && user != null) {
            Row(
                Modifier.fillMaxWidth().padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (user?.imageUrl != null) {
                    AsyncImage(user?.imageUrl, null, Modifier.size(64.dp).clip(CircleShape))
                } else {
                    NuIcon(NotesupIcons.Account, null, Modifier.size(64.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(user?.email ?: stringResource(R.string.signed_in), style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (user?.viaGoogle == true) stringResource(R.string.via_google) else stringResource(R.string.via_email),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            ListItem(headlineContent = { Text(stringResource(R.string.signed_in)) })
        }
        if (!signedIn) TextButtonRow(stringResource(R.string.sign_in), onSignIn)
        if (signedIn) TextButtonRow(stringResource(R.string.sign_out)) { confirm = true }
        if (signedIn) {
            Text(
                stringResource(R.string.delete_account),
                modifier = Modifier.fillMaxWidth().clickable { deleteConfirm = true }.padding(20.dp, 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text(stringResource(R.string.sign_out_q)) },
            text = { Text(stringResource(R.string.sign_out_body)) },
            confirmButton = {
                TextButton(onClick = { confirm = false; vm.signOut(onSignOut) }) { Text(stringResource(R.string.sign_out)) }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
    if (deleteConfirm) {
        AlertDialog(
            onDismissRequest = { deleteConfirm = false; deleteTyped = "" },
            title = { Text(stringResource(R.string.delete_account_q)) },
            text = {
                Column {
                    Text(stringResource(R.string.delete_account_body))
                    androidx.compose.material3.OutlinedTextField(
                        value = deleteTyped,
                        onValueChange = { deleteTyped = it },
                        label = { Text(stringResource(R.string.type_delete)) },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { deleteConfirm = false; deleteTyped = ""; vm.deleteAccount(onSignOut) },
                    enabled = deleteTyped == "DELETE",
                ) { Text(stringResource(R.string.delete_account)) }
            },
            dismissButton = { TextButton(onClick = { deleteConfirm = false; deleteTyped = "" }) { Text(stringResource(R.string.cancel)) } },
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
fun AccountSheet(
    onDismiss: () -> Unit,
    onSignIn: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit,
    vm: AccountViewModel = hiltViewModel(),
) {
    val user by vm.user.collectAsStateWithLifecycle(null)
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(20.dp).padding(bottom = 24.dp)) {
            if (user != null) {
                if (user?.imageUrl != null) {
                    AsyncImage(user?.imageUrl, null, Modifier.size(56.dp).clip(CircleShape))
                } else {
                    NuIcon(NotesupIcons.Account, null, Modifier.size(56.dp))
                }
                Text(user?.email ?: stringResource(R.string.signed_in), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
                Text(
                    if (user?.viaGoogle == true) stringResource(R.string.via_google) else stringResource(R.string.via_email),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                NuIcon(NotesupIcons.Account, null, Modifier.height(64.dp).fillMaxWidth())
                Text(stringResource(R.string.sign_in_to_sync), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.sign_in_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButtonRow(stringResource(R.string.sign_in), onSignIn)
            }
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

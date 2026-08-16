package com.notesup.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.ui.account.AboutScreen
import com.notesup.app.ui.account.AccountSheet
import com.notesup.app.ui.account.AppearanceScreen
import com.notesup.app.ui.account.AuthCodeScreen
import com.notesup.app.ui.account.AuthScreen
import com.notesup.app.ui.account.FocusSettingsScreen
import com.notesup.app.ui.account.ManageAccountScreen
import com.notesup.app.ui.account.PaperSettingsScreen
import com.notesup.app.ui.account.PrivacyScreen
import com.notesup.app.ui.account.SettingsScreen
import com.notesup.app.ui.account.TrashScreen
import com.notesup.app.ui.account.TypeSettingsScreen
import com.notesup.app.ui.editor.EditorScreen
import com.notesup.app.ui.home.HomeScreen
import com.notesup.app.ui.onboarding.WelcomeScreen
import com.notesup.app.ui.project.ProjectScreen
import com.notesup.app.ui.search.SearchScreen
import android.net.Uri
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun NotesupNav(
    prefs: NotesupPrefs,
    initialDeepLink: String? = null,
) {
    val onboarded = remember {
        runBlocking { prefs.onboardingDone.first() }
    }
    val start: NavKey = if (onboarded) Home else Welcome
    val backStack = rememberNavBackStack(start)
    var account by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun push(key: NavKey) {
        backStack.add(key)
    }
    fun pop() {
        if (backStack.size > 1) backStack.removeLastOrNull()
    }
    fun finishOnboarding() {
        scope.launch {
            prefs.setOnboardingDone(true)
            backStack.add(Home)
            while (backStack.size > 1) backStack.removeAt(0)
        }
    }

    var handledLink by rememberSaveable { mutableStateOf<String?>(null) }
    androidx.compose.runtime.LaunchedEffect(initialDeepLink) {
        val link = initialDeepLink ?: return@LaunchedEffect
        if (link == handledLink) return@LaunchedEffect
        handledLink = link
        val uri = runCatching { Uri.parse(link) }.getOrNull() ?: return@LaunchedEffect
        if (uri.scheme != "notesup") return@LaunchedEffect
        when (uri.host) {
            "note" -> uri.pathSegments.firstOrNull()?.let { push(Editor(it, false)) }
            "project" -> uri.pathSegments.firstOrNull()?.let { push(ProjectDest(it)) }
            "search" -> push(Search)
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { pop() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Welcome> {
                WelcomeScreen(
                    onStartWriting = ::finishOnboarding,
                    onSignIn = { push(Auth) },
                )
            }
            entry<Home> {
                HomeScreen(
                    vm = hiltViewModel(),
                    onOpenNote = { id, created -> push(Editor(id, created)) },
                    onSearch = { push(Search) },
                    onAccount = { account = true },
                    onProject = { push(ProjectDest(it)) },
                    onSettings = { push(Settings) },
                    showRail = false,
                )
            }
            entry<Auth> {
                val vm = hiltViewModel<com.notesup.app.ui.account.AuthViewModel>()
                val err by vm.error.collectAsStateWithLifecycle()
                AuthScreen(
                    onBack = { pop() },
                    onContinueWithout = ::finishOnboarding,
                    onEmail = { vm.startEmail(it) { push(AuthCode(it)) } },
                    error = err,
                    onGoogle = { vm.google(::finishOnboarding) },
                    onPasskey = { vm.passkey(::finishOnboarding) },
                )
            }
            entry<AuthCode> { key ->
                val vm = hiltViewModel<com.notesup.app.ui.account.AuthViewModel>()
                val err by vm.error.collectAsStateWithLifecycle()
                val busy by vm.busy.collectAsStateWithLifecycle()
                val resend by vm.resendIn.collectAsStateWithLifecycle()
                AuthCodeScreen(
                    email = key.email,
                    onBack = { pop() },
                    onVerify = { vm.verify(it, ::finishOnboarding) },
                    verifying = busy,
                    error = err,
                    onResend = { vm.resend(key.email) },
                    resendIn = resend,
                )
            }
            entry<Search> {
                SearchScreen(onBack = { pop() }, onOpen = { push(Editor(it, false)) })
            }
            entry<Editor> { key ->
                EditorScreen(
                    noteId = key.noteId,
                    vm = hiltViewModel(),
                    created = key.created,
                    onBack = { pop() },
                    onMissing = { pop() },
                )
            }
            entry<ProjectDest> { key ->
                ProjectScreen(projectId = key.projectId, onBack = { pop() }, onOpenNote = { id, c -> push(Editor(id, c)) })
            }
            entry<Settings> {
                SettingsScreen(
                    onBack = { pop() },
                    onAppearance = { push(Appearance) },
                    onType = { push(TypeSettings) },
                    onPaper = { push(PaperSettings) },
                    onFocus = { push(FocusSettings) },
                    onTrash = { push(Trash) },
                    onAccount = { push(ManageAccount) },
                    onPrivacy = { push(Privacy) },
                    onAbout = { push(About) },
                )
            }
            entry<Appearance> { AppearanceScreen(onBack = { pop() }) }
            entry<TypeSettings> { TypeSettingsScreen(onBack = { pop() }) }
            entry<PaperSettings> { PaperSettingsScreen(onBack = { pop() }) }
            entry<FocusSettings> { FocusSettingsScreen(onBack = { pop() }) }
            entry<Trash> { TrashScreen(onBack = { pop() }) }
            entry<ManageAccount> {
                ManageAccountScreen(onBack = { pop() }, onSignIn = { push(Auth) }, onSignOut = { pop() })
            }
            entry<Privacy> { PrivacyScreen { pop() } }
            entry<About> { AboutScreen { pop() } }
        },
    )

    if (account) {
        AccountSheet(
            onDismiss = { account = false },
            onSignIn = { account = false; push(Auth) },
            onSettings = { account = false; push(Settings) },
            onAbout = { account = false; push(About) },
        )
    }
}

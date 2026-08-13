package com.notesup.app.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("notesup")

class NotesupPrefs(private val context: Context) {
    private object Keys {
        val theme = stringPreferencesKey("theme")
        val installId = stringPreferencesKey("installId")
        val onboardingDone = booleanPreferencesKey("onboarding_done")
        val syncPaused = booleanPreferencesKey("sync_paused")
        val homeView = stringPreferencesKey("home_view")
        val sortChecked = booleanPreferencesKey("sort_checked")
        val lockNew = booleanPreferencesKey("lock_new")
        val lockScreenHistory = booleanPreferencesKey("lock_screen_history")
        val focus = stringPreferencesKey("focus")
        val defaultPaper = stringPreferencesKey("default_paper")
        val defaultFont = stringPreferencesKey("default_font")
        val bodySize = stringPreferencesKey("body_size")
        val appTheme = stringPreferencesKey("app_theme")
    }

    val theme: Flow<String> = context.dataStore.data.map { it[Keys.theme] ?: "system" }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.onboardingDone] ?: false }
    val syncPaused: Flow<Boolean> = context.dataStore.data.map { it[Keys.syncPaused] ?: false }
    val homeView: Flow<String> = context.dataStore.data.map { it[Keys.homeView] ?: "grid" }
    val sortChecked: Flow<Boolean> = context.dataStore.data.map { it[Keys.sortChecked] ?: false }
    val lockNew: Flow<Boolean> = context.dataStore.data.map { it[Keys.lockNew] ?: false }
    val lockScreenHistory: Flow<Boolean> = context.dataStore.data.map { it[Keys.lockScreenHistory] ?: false }
    val focus: Flow<String> = context.dataStore.data.map { it[Keys.focus] ?: "auto" }
    val defaultPaper: Flow<String> = context.dataStore.data.map { it[Keys.defaultPaper] ?: "blank" }
    val defaultFont: Flow<String> = context.dataStore.data.map { it[Keys.defaultFont] ?: "roboto_flex" }
    val bodySize: Flow<String> = context.dataStore.data.map { it[Keys.bodySize] ?: "M" }
    val appTheme: Flow<String> = context.dataStore.data.map { it[Keys.appTheme] ?: "dynamic" }

    suspend fun installId(): String {
        val existing = context.dataStore.data.first()[Keys.installId]
        if (existing != null) return existing
        val id = UUID.randomUUID().toString()
        context.dataStore.edit { it[Keys.installId] = id }
        return id
    }

    suspend fun setOnboardingDone(done: Boolean = true) {
        context.dataStore.edit { it[Keys.onboardingDone] = done }
    }

    suspend fun setSyncPaused(paused: Boolean) {
        context.dataStore.edit { it[Keys.syncPaused] = paused }
    }

    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[Keys.theme] = value }
    }

    suspend fun setHomeView(value: String) {
        context.dataStore.edit { it[Keys.homeView] = value }
    }

    suspend fun setSortChecked(value: Boolean) {
        context.dataStore.edit { it[Keys.sortChecked] = value }
    }

    suspend fun setLockNew(value: Boolean) {
        context.dataStore.edit { it[Keys.lockNew] = value }
    }

    suspend fun setLockScreenHistory(value: Boolean) {
        context.dataStore.edit { it[Keys.lockScreenHistory] = value }
    }

    suspend fun setFocus(value: String) {
        context.dataStore.edit { it[Keys.focus] = value }
    }

    suspend fun setDefaultPaper(value: String) {
        context.dataStore.edit { it[Keys.defaultPaper] = value }
    }

    suspend fun setDefaultFont(value: String) {
        context.dataStore.edit { it[Keys.defaultFont] = value }
    }

    suspend fun setBodySize(value: String) {
        context.dataStore.edit { it[Keys.bodySize] = value }
    }

    suspend fun setAppTheme(value: String) {
        context.dataStore.edit { it[Keys.appTheme] = value }
    }
}

package com.notesup.app.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Welcome : NavKey
@Serializable data object Home : NavKey
@Serializable data object Auth : NavKey
@Serializable data class AuthCode(val email: String) : NavKey
@Serializable data object Search : NavKey
@Serializable data class Editor(val noteId: String, val created: Boolean = false) : NavKey
@Serializable data class ProjectDest(val projectId: String) : NavKey
@Serializable data object Settings : NavKey
@Serializable data object Appearance : NavKey
@Serializable data object TypeSettings : NavKey
@Serializable data object PaperSettings : NavKey
@Serializable data object FocusSettings : NavKey
@Serializable data object Trash : NavKey
@Serializable data object ManageAccount : NavKey
@Serializable data object Privacy : NavKey
@Serializable data object About : NavKey

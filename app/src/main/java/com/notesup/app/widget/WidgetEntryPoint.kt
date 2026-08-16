package com.notesup.app.widget

import com.notesup.app.data.local.NoteDao
import com.notesup.app.data.local.ProjectDao
import com.notesup.app.data.prefs.NotesupPrefs
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun noteDao(): NoteDao
    fun projectDao(): ProjectDao
    fun prefs(): NotesupPrefs
}

package com.notesup.app.di

import android.content.Context
import androidx.room.Room
import com.notesup.app.data.local.InkDao
import com.notesup.app.data.local.MediaDao
import com.notesup.app.data.local.NoteDao
import com.notesup.app.data.local.NotesupDb
import com.notesup.app.data.local.ProjectDao
import com.notesup.app.data.local.SyncQueueDao
import com.notesup.app.data.prefs.NotesupPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun db(@ApplicationContext context: Context): NotesupDb =
        Room.databaseBuilder(context, NotesupDb::class.java, "notesup.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun notes(db: NotesupDb): NoteDao = db.notes()
    @Provides fun projects(db: NotesupDb): ProjectDao = db.projects()
    @Provides fun media(db: NotesupDb): MediaDao = db.media()
    @Provides fun ink(db: NotesupDb): InkDao = db.ink()
    @Provides fun queue(db: NotesupDb): SyncQueueDao = db.syncQueue()

    @Provides
    @Singleton
    fun prefs(@ApplicationContext context: Context): NotesupPrefs = NotesupPrefs(context)
}

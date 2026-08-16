package com.notesup.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.notesup.app.data.local.entities.InkEntity
import com.notesup.app.data.local.entities.MediaEntity
import com.notesup.app.data.local.entities.NoteEntity
import com.notesup.app.data.local.entities.NoteFts
import com.notesup.app.data.local.entities.ProjectEntity
import com.notesup.app.data.local.entities.SyncQueueEntity

@Database(
    entities = [
        NoteEntity::class,
        NoteFts::class,
        ProjectEntity::class,
        MediaEntity::class,
        InkEntity::class,
        SyncQueueEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class NotesupDb : RoomDatabase() {
    abstract fun notes(): NoteDao
    abstract fun projects(): ProjectDao
    abstract fun media(): MediaDao
    abstract fun ink(): InkDao
    abstract fun syncQueue(): SyncQueueDao
}

package com.notesup.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.notesup.app.data.local.entities.InkEntity
import com.notesup.app.data.local.entities.MediaEntity
import com.notesup.app.data.local.entities.NoteEntity
import com.notesup.app.data.local.entities.NoteFts
import com.notesup.app.data.local.entities.ProjectEntity
import com.notesup.app.data.local.entities.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE deletedAt IS NULL ORDER BY pinned DESC, updatedAt DESC")
    fun observeAlive(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE deletedAt IS NULL AND pinned = 1 ORDER BY updatedAt DESC")
    fun observePinned(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE deletedAt IS NULL ORDER BY updatedAt DESC")
    fun observeRecent(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE deletedAt IS NULL AND projectId IS :projectId ORDER BY updatedAt DESC")
    fun observeInProject(projectId: String?): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun observeTrash(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun get(id: String): NoteEntity?

    @Query("SELECT * FROM notes WHERE id = :id")
    fun observe(id: String): Flow<NoteEntity?>

    @Query("SELECT COUNT(*) FROM notes WHERE deletedAt IS NULL")
    suspend fun countAlive(): Int

    @Query("SELECT COUNT(*) FROM notes WHERE projectId IS NULL AND deletedAt IS NULL")
    fun observeInboxCount(): Flow<Int>

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Update
    suspend fun update(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFts(fts: NoteFts)

    @Query("DELETE FROM notes_fts WHERE noteId = :noteId")
    suspend fun deleteFts(noteId: String)

    @Query("SELECT notes.* FROM notes JOIN notes_fts ON notes.id = notes_fts.noteId WHERE notes_fts.plaintext MATCH :q AND notes.deletedAt IS NULL")
    suspend fun search(q: String): List<NoteEntity>

    @Query("DELETE FROM notes WHERE deletedAt IS NOT NULL AND deletedAt < :before")
    suspend fun purgeTrash(before: Long)

    @Query("DELETE FROM notes WHERE deletedAt IS NOT NULL")
    suspend fun emptyTrash()

    @Query("UPDATE notes SET projectId = NULL WHERE projectId = :projectId")
    suspend fun moveProjectToInbox(projectId: String)

    @Transaction
    suspend fun upsertWithFts(note: NoteEntity, fts: NoteFts?) {
        upsert(note)
        deleteFts(note.id)
        if (fts != null && !note.locked) {
            upsertFts(fts)
        }
    }
}

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE deletedAt IS NULL ORDER BY `order` ASC")
    fun observeAlive(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun get(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(project: ProjectEntity)

    @Query("SELECT COUNT(*) FROM notes WHERE projectId = :id AND deletedAt IS NULL")
    suspend fun noteCount(id: String): Int
}

@Dao
interface MediaDao {
    @Query("SELECT * FROM media WHERE id = :id")
    suspend fun get(id: String): MediaEntity?

    @Query("SELECT * FROM media WHERE noteId = :noteId")
    suspend fun forNote(noteId: String): List<MediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(media: MediaEntity)
}

@Dao
interface InkDao {
    @Query("SELECT * FROM ink WHERE id = :id")
    suspend fun get(id: String): InkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(ink: InkEntity)
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY createdAt ASC")
    suspend fun all(): List<SyncQueueEntity>

    @Insert
    suspend fun enqueue(item: SyncQueueEntity)

    @Query("DELETE FROM sync_queue WHERE kind = :kind AND localId = :localId")
    suspend fun removeFor(kind: String, localId: String)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun remove(id: Long)

    /** Collapse repeated edits of the same entity into a single pending row. */
    @Transaction
    suspend fun enqueueLatest(item: SyncQueueEntity) {
        removeFor(item.kind, item.localId)
        enqueue(item)
    }
}

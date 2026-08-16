package com.notesup.app.data.repo

import com.notesup.app.data.local.NoteDao
import com.notesup.app.data.local.ProjectDao
import com.notesup.app.data.local.toDomain
import com.notesup.app.data.local.toEntity
import com.notesup.app.data.prefs.NotesupPrefs
import com.notesup.app.domain.model.Project
import com.notesup.app.domain.model.ProjectId
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ProjectRepository @Inject constructor(
    private val projects: ProjectDao,
    private val notes: NoteDao,
    private val prefs: NotesupPrefs,
) {
    fun observe(): Flow<List<Project>> = projects.observeAlive().map { it.map { p -> p.toDomain() } }

    suspend fun get(id: ProjectId): Project? = projects.get(id.raw)?.toDomain()

    suspend fun count(id: ProjectId): Int = projects.noteCount(id.raw)

    suspend fun create(name: String, hue: Int, emoji: String?): Project {
        val now = Instant.now()
        val p = Project(
            id = ProjectId.random(),
            remoteId = null,
            name = name.trim(),
            hue = hue,
            emoji = emoji,
            order = 0,
            createdAt = now,
            updatedAt = now,
            deletedAt = null,
            rev = 1,
            writerId = prefs.installId(),
        )
        projects.upsert(p.toEntity())
        return p
    }

    suspend fun save(project: Project) {
        projects.upsert(
            project.copy(
                updatedAt = Instant.now(),
                rev = project.rev + 1,
                writerId = prefs.installId(),
            ).toEntity(),
        )
    }

    suspend fun delete(id: ProjectId) {
        val existing = projects.get(id.raw) ?: return
        notes.moveProjectToInbox(id.raw)
        projects.upsert(
            existing.copy(
                deletedAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                rev = existing.rev + 1,
                writerId = prefs.installId(),
            ),
        )
    }
}

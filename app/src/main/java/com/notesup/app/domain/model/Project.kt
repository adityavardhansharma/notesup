package com.notesup.app.domain.model

import java.time.Instant

data class Project(
    val id: ProjectId,
    val remoteId: String?,
    val name: String,
    val hue: Int,
    val emoji: String?,
    val order: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?,
    val rev: Long,
    val writerId: String,
)

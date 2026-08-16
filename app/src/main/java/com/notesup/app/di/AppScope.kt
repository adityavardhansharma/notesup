package com.notesup.app.di

import javax.inject.Qualifier

/**
 * Marks the process-lifetime [kotlinx.coroutines.CoroutineScope] used for work that
 * must outlive the UI component that triggered it (e.g. persisting an edit while the
 * editor is being torn down on back navigation).
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

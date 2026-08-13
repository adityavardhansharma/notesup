package com.notesup.app.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.notesup.app.data.remote.SyncCoordinator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val sync: SyncCoordinator,
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        sync.start()
        return Result.success()
    }
}

package com.notesup.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.notesup.app.data.auth.AuthRepository
import com.notesup.app.data.remote.SyncCoordinator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NotesupApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var auth: AuthRepository
    @Inject lateinit var sync: SyncCoordinator

    override fun onCreate() {
        super.onCreate()
        auth.initialize()
        sync.start()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}

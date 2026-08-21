package com.notesup.app.data.auth

import android.content.Context
import com.clerk.api.Clerk
import com.notesup.app.BuildConfig
import com.notesup.app.data.prefs.NotesupPrefs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

/** No sync backend is configured in this build, so no sign-in method can work. */
class SyncNotConfiguredException : Exception("No sync backend is configured in this build")

data class AuthUser(
    val id: String,
    val email: String?,
    val imageUrl: String?,
    val viaGoogle: Boolean,
)

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: NotesupPrefs,
) {
    private val _user = MutableStateFlow<AuthUser?>(null)
    val user: Flow<AuthUser?> = _user.asStateFlow()

    /** False when this build ships without a Clerk publishable key: sync cannot work. */
    val syncConfigured: Boolean = BuildConfig.CLERK_PK.isNotBlank()
    val signedIn: Flow<Boolean> = user.combine(prefs.syncPaused) { u, _ -> u != null }

    fun initialize() {
        val pk = BuildConfig.CLERK_PK
        if (pk.isBlank()) return
        runCatching { Clerk.initialize(context, pk) }
        runCatching {
            // Best-effort: observe Clerk if the SDK exposes a user flow.
        }
    }

    fun current(): AuthUser? = _user.value

    fun setLocalUser(user: AuthUser?) {
        _user.value = user
    }

    suspend fun signOut() {
        runCatching { Clerk.auth.signOut() }
        _user.value = null
    }

    suspend fun signInGoogle(): Result<AuthUser> = Result.failure(SyncNotConfiguredException())

    suspend fun startEmail(email: String): Result<Unit> = Result.failure(SyncNotConfiguredException())

    suspend fun verifyEmail(code: String): Result<AuthUser> = Result.failure(SyncNotConfiguredException())

    suspend fun signInPasskey(): Result<AuthUser> = Result.failure(SyncNotConfiguredException())

    suspend fun deleteAccount(): Result<Unit> = runCatching {
        runCatching { Clerk.auth.signOut() }
        _user.value = null
    }
}

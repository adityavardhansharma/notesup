package com.notesup.app.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.R
import com.notesup.app.data.auth.AuthRepository
import com.notesup.app.data.auth.SyncNotConfiguredException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {
    /** User-facing message as a string resource id, never a developer message. */
    val error = MutableStateFlow<Int?>(null)

    /** False when this build has no sync backend, so sign-in is offered as unavailable. */
    val syncConfigured: Boolean = auth.syncConfigured
    val busy = MutableStateFlow(false)
    val resendIn = MutableStateFlow(0)
    private var countdown: Job? = null

    fun startEmail(email: String, onSent: () -> Unit) {
        viewModelScope.launch {
            busy.value = true
            error.value = null
            val result = auth.startEmail(email)
            busy.value = false
            result.fold(
                onSuccess = {
                    startCountdown()
                    onSent()
                },
                onFailure = { error.value = messageFor(it) },
            )
        }
    }

    fun verify(code: String, onOk: () -> Unit) {
        viewModelScope.launch {
            busy.value = true
            error.value = null
            val result = auth.verifyEmail(code)
            busy.value = false
            result.fold(
                onSuccess = { onOk() },
                onFailure = {
                    error.value = if (it is SyncNotConfiguredException) R.string.sync_not_configured else R.string.code_mismatch
                },
            )
        }
    }

    fun google(onOk: () -> Unit) {
        viewModelScope.launch {
            busy.value = true
            error.value = null
            val result = auth.signInGoogle()
            busy.value = false
            result.fold(onSuccess = { onOk() }, onFailure = { error.value = messageFor(it) })
        }
    }

    fun passkey(onOk: () -> Unit) {
        viewModelScope.launch {
            busy.value = true
            error.value = null
            val result = auth.signInPasskey()
            busy.value = false
            result.fold(onSuccess = { onOk() }, onFailure = { error.value = messageFor(it) })
        }
    }

    private fun messageFor(err: Throwable): Int =
        if (err is SyncNotConfiguredException) R.string.sync_not_configured else R.string.sign_in_failed

    fun resend(email: String) {
        startEmail(email) { }
    }

    private fun startCountdown() {
        countdown?.cancel()
        countdown = viewModelScope.launch {
            resendIn.value = 30
            while (resendIn.value > 0) {
                delay(1000)
                resendIn.value = resendIn.value - 1
            }
        }
    }
}

package com.notesup.app.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesup.app.data.auth.AuthRepository
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
    val error = MutableStateFlow<String?>(null)
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
                onFailure = { error.value = it.message ?: "Couldn't sign in." },
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
                onFailure = { error.value = "That code didn't match" },
            )
        }
    }

    fun google(onOk: () -> Unit) {
        viewModelScope.launch {
            busy.value = true
            error.value = null
            val result = auth.signInGoogle()
            busy.value = false
            result.fold(onSuccess = { onOk() }, onFailure = { error.value = it.message })
        }
    }

    fun passkey(onOk: () -> Unit) {
        viewModelScope.launch {
            busy.value = true
            error.value = null
            val result = auth.signInPasskey()
            busy.value = false
            result.fold(onSuccess = { onOk() }, onFailure = { error.value = it.message })
        }
    }

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

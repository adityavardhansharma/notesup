package com.notesup.app.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesup.app.R
import com.notesup.app.ui.common.NotesupIcons
import com.notesup.app.ui.common.NuIcon
import com.notesup.app.ui.common.rememberHaptics
import com.notesup.app.ui.theme.PaperGrain
import com.notesup.app.ui.theme.Stadium

@Composable
fun AuthScreen(
    onBack: () -> Unit,
    onContinueWithout: () -> Unit,
    onEmail: (String) -> Unit,
    error: Int? = null,
    onGoogle: (() -> Unit)? = null,
    onPasskey: (() -> Unit)? = null,
    syncConfigured: Boolean = true,
) {
    var email by remember { mutableStateOf("") }
    var notice by remember { mutableStateOf<String?>(null) }
    val googleUnavailable = stringResource(R.string.google_unavailable)
    val haptics = rememberHaptics()
    PaperGrain(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back))
            }
            Spacer(Modifier.height(16.dp))
            Text("Notesup", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.sign_in_to_sync), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(if (syncConfigured) R.string.sign_in_body else R.string.sync_not_configured),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp),
            )
            Spacer(Modifier.height(32.dp))
            OutlinedButton(
                onClick = {
                    if (onGoogle != null) onGoogle() else {
                        haptics.reject()
                        notice = googleUnavailable
                    }
                },
                enabled = syncConfigured,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                NuIcon(NotesupIcons.Google, null, Modifier.size(20.dp), tint = androidx.compose.ui.graphics.Color.Unspecified)
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.continue_google),
                    color = if (syncConfigured) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f))
                Text(stringResource(R.string.or), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email_hint)) },
                singleLine = true,
                enabled = syncConfigured,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Go),
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onEmail(email.trim()) },
                enabled = syncConfigured && email.contains("@"),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = Stadium,
            ) { Text(stringResource(R.string.continue_email)) }
            TextButton(enabled = syncConfigured, onClick = {
                if (onPasskey != null) onPasskey() else {
                    haptics.reject()
                    notice = googleUnavailable
                }
            }) {
                NuIcon(NotesupIcons.Passkey, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.use_passkey))
            }
            (error?.let { stringResource(it) } ?: notice)?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { haptics.confirm(); onContinueWithout() }) {
                Text(stringResource(R.string.continue_without), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.auth_legal),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 280.dp),
            )
        }
    }
}

@Composable
fun AuthCodeScreen(
    email: String,
    onBack: () -> Unit,
    onVerify: (String) -> Unit,
    verifying: Boolean = false,
    error: Int? = null,
    onResend: () -> Unit = {},
    resendIn: Int = 0,
) {
    var code by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            NuIcon(NotesupIcons.Back, stringResource(R.string.cd_back))
        }
        Text(stringResource(R.string.check_email), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.code_sent), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(email, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            repeat(6) { i ->
                val ch = code.getOrNull(i)?.toString() ?: ""
                Box(
                    Modifier
                        .size(44.dp, 52.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(ch, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        OutlinedTextField(
            value = code,
            onValueChange = {
                if (it.length <= 6 && it.all(Char::isDigit) && !verifying) {
                    code = it
                    if (it.length == 6) onVerify(it)
                }
            },
            enabled = !verifying,
            label = { Text(stringResource(R.string.code_cd)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        )
        if (verifying) {
            androidx.compose.material3.CircularProgressIndicator(Modifier.size(24.dp).padding(top = 12.dp))
        }
        error?.let {
            Text(stringResource(it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }
        if (resendIn > 0) {
            Text(stringResource(R.string.resend_in, resendIn), style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 16.dp))
        } else {
            TextButton(onClick = onResend, modifier = Modifier.padding(top = 8.dp)) {
                Text(stringResource(R.string.resend))
            }
        }
    }
}

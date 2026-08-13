package com.notesup.app.ui.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesup.app.R
import com.notesup.app.ui.common.rememberHaptics
import com.notesup.app.ui.theme.PaperGrain
import com.notesup.app.ui.theme.Stadium

@Composable
fun WelcomeScreen(
    onStartWriting: () -> Unit,
    onSignIn: () -> Unit,
) {
    val haptics = rememberHaptics()
    val context = LocalContext.current
    BackHandler { (context as? android.app.Activity)?.finish() }
    PaperGrain(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Notesup", style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.welcome_line), style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.welcome_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 280.dp),
                    )
                }
            }
            Button(
                onClick = { haptics.confirm(); onStartWriting() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = Stadium,
            ) { Text(stringResource(R.string.start_writing)) }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onSignIn) { Text(stringResource(R.string.sign_in_to_sync)) }
        }
    }
}

package com.notesup.app.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.notesup.app.MainActivity

fun notesupUri(path: String): Uri = Uri.parse("notesup://$path")

fun notesupViewIntent(context: Context, uri: Uri): Intent =
    Intent(Intent.ACTION_VIEW, uri)
        .setClass(context, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

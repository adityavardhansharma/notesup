package com.notesup.app.ui.common

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.content.getSystemService

class NotesupHaptics(private val view: View) {
    private val vibrator = view.context.getSystemService<Vibrator>()

    fun confirm() = play(HapticFeedbackConstants.CONFIRM, rich = true)
    fun reject() = play(HapticFeedbackConstants.REJECT)
    fun tick() = play(HapticFeedbackConstants.CLOCK_TICK)
    fun longPress() = play(HapticFeedbackConstants.LONG_PRESS)
    fun gestureStart() {
        if (Build.VERSION.SDK_INT >= 34) play(HapticFeedbackConstants.GESTURE_START) else tick()
    }
    fun gestureEnd() {
        if (Build.VERSION.SDK_INT >= 34) play(HapticFeedbackConstants.GESTURE_END) else confirm()
    }

    private fun play(constant: Int, rich: Boolean = false) {
        if (rich && Build.VERSION.SDK_INT >= 30) {
            val composed = runCatching {
                VibrationEffect.startComposition()
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_TICK)
                    .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                    .compose()
            }.getOrNull()
            if (composed != null) {
                vibrator?.vibrate(composed)
                return
            }
        }
        view.performHapticFeedback(constant)
    }
}

@Composable
fun rememberHaptics(): NotesupHaptics {
    val view = LocalView.current
    return remember(view) { NotesupHaptics(view) }
}

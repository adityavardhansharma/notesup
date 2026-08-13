package com.notesup.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collect(packageName = "com.notesup.app", includeInStartupProfile = true) {
            pressHome()
            startActivityAndWait()
            device.wait(Until.hasObject(By.text("Start writing")), 5_000)
            device.findObject(By.text("Start writing"))?.click()
            device.wait(Until.hasObject(By.desc("New note")), 5_000)
            device.findObject(By.desc("New note"))?.click()
            device.waitForIdle()
            device.pressBack()
        }
    }
}

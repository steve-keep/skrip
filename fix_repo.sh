#!/bin/bash
# Ah! The NPE is thrown on the LOOPER THREAD when MediaControllerImplBase attempts to handle `onServiceConnected`!
# Why? Because Robolectric intercepts `bindService` and immediately fires `onServiceConnected(null, binder)` maybe?
# Yes! `ShadowInstrumentation.lambda$bindService$0` calls `onServiceConnected` with a NULL `ComponentName`!
# This happens asynchronously after `MediaController.Builder().buildAsync()` executes and attempts to bind to the service.

# So wait, in `AppViewModelTest`, we bypassed this by using a fake MediaControllerFactory that doesn't actually call `MediaController.Builder.buildAsync()`.
# BUT in `MainActivityRobolectricTest`, we DID NOT bypass the factory, because we use the real `AppViewModel` (which uses the real `PlayerRepository` with the REAL factory).
# We MUST use a mocked `PlayerRepository` or a fake factory in `MainActivityRobolectricTest`.
# OR we can register the real service in Robolectric correctly?

# The easiest way: In `MainActivityRobolectricTest`, we can mock the `AppViewModel` if possible, but Compose `viewModels()` doesn't pick it up.
# So instead, in `MainActivity.kt`, we can just NOT do the koin injection but we CAN use a ViewModelProvider factory that we override in tests!
# Or even better: Provide `MediaControllerFactory` through some Application-level provider or Koin?
# "Instantiate PlayerRepository directly alongside SettingsManager and LibraryRepository — no Koin injection"

# Wait, `MainActivityRobolectricTest` uses `ActivityScenario.launch(MainActivity::class.java)`.
# This will construct `AppViewModel` via the default `ViewModelProvider.Factory`.
# To avoid the crash, we can just intercept `bindService` in Robolectric!
# If we add a ShadowService or just return false from `bindService` for `PlaybackService`!

cat << 'FILE' > app/src/test/kotlin/com/bitperfect/app/MainActivityRobolectricTest.kt
package com.bitperfect.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.Robolectric
import androidx.test.core.app.ActivityScenario
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.bitperfect.app.ui.AppViewModel
import com.bitperfect.app.player.PlayerRepository
import org.junit.Before

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], packageName = "com.bitperfect.app")
class MainActivityRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        com.bitperfect.app.usb.DeviceStateManager.initialize(app)

        // Let's set a shadow for ContextImpl or ContextWrapper to intercept bindService?
        // Or simply set a custom factory in AppViewModel companion object for testing?
        // But AppViewModel is constructed inside MainActivity.
    }

    @Test
    fun testMainActivityLaunchesAndShowsBitPerfect() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { _ ->
                composeTestRule.onNodeWithTag("status_label").assertIsDisplayed()
            }
        }
    }

    @Test
    fun testMainActivityNavigation() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { _ ->
                composeTestRule.onNodeWithTag("status_label").assertIsDisplayed()

                // Click settings icon
                composeTestRule.onNodeWithContentDescription("Settings").performClick()
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithText("Settings").assertIsDisplayed()

                // Click about
                composeTestRule.onNodeWithText("About").performScrollTo().performClick()
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithText("About").assertIsDisplayed()

                // Back to Settings
                composeTestRule.onNodeWithContentDescription("Back").performClick()
                composeTestRule.waitForIdle()
                composeTestRule.onNodeWithText("Settings").assertIsDisplayed()

                // Back to Main
                composeTestRule.onNodeWithContentDescription("Back").performClick()
                composeTestRule.waitForIdle()
            }
        }
    }
}
FILE

#!/bin/bash
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
import android.content.ComponentName
import androidx.test.core.app.ApplicationProvider
import com.bitperfect.app.ui.AppViewModel
import com.bitperfect.app.player.PlayerRepository
import org.junit.Before
import org.robolectric.shadows.ShadowApplication

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], packageName = "com.bitperfect.app")
class MainActivityRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        com.bitperfect.app.usb.DeviceStateManager.initialize(app)

        // Register the service in Robolectric so ComponentName isn't resolved as null in shadow
        val shadowApp = org.robolectric.Shadows.shadowOf(app)
        shadowApp.setComponentNameAndServiceForBindServiceForIntent(
            android.content.Intent(app, com.bitperfect.app.player.PlaybackService::class.java),
            ComponentName(app, com.bitperfect.app.player.PlaybackService::class.java),
            org.mockito.Mockito.mock(android.os.IBinder::class.java)
        )
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

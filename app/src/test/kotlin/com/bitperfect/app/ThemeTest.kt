package com.bitperfect.app

import org.junit.Test
import org.junit.Assert.*
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import com.bitperfect.app.ui.theme.BitPerfectTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.material3.Text
import org.junit.Rule

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testBitPerfectTheme_LightMode() {
        composeTestRule.setContent {
            BitPerfectTheme(darkTheme = false) {
                Text("Test")
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun testBitPerfectTheme_DarkMode() {
        composeTestRule.setContent {
            BitPerfectTheme(darkTheme = true) {
                Text("Test")
            }
        }
        composeTestRule.waitForIdle()
    }
}

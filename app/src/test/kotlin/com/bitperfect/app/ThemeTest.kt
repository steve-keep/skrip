package com.bitperfect.app

import org.junit.Test
import org.junit.Assert.*
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ThemeTest {
    @Test
    fun verifyThemeResources() {
        val app = org.robolectric.RuntimeEnvironment.getApplication()

        // This exercises our new themes.xml via resource loading to get coverage points
        val themeId = app.resources.getIdentifier("Theme.App.Starting", "style", app.packageName)
        assertNotEquals("Theme should be found", 0, themeId)
    }
}

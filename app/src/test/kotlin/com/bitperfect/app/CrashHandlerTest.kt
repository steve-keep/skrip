package com.bitperfect.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CrashHandlerTest {

    @Test
    fun `uncaughtException saves stack trace to SharedPreferences`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("crash_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit() // start clean

        val crashHandler = CrashHandler(context)
        val exception = RuntimeException("Test Crash")

        var defaultHandlerCalled = false
        val mockDefaultHandler = Thread.UncaughtExceptionHandler { _, _ ->
            defaultHandlerCalled = true
        }

        val originalDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        try {
            // Set the default handler to our mock so we can verify it's called
            Thread.setDefaultUncaughtExceptionHandler(mockDefaultHandler)

            // Because CrashHandler captures the default handler at instantiation time, we need to instantiate it AFTER setting our mock default handler to test that behavior accurately.
            // Wait, actually CrashHandler captures it on initialization. Let's recreate it.
            val handlerWithMock = CrashHandler(context)

            handlerWithMock.uncaughtException(Thread.currentThread(), exception)

            // Verify SharedPreferences
            val savedTrace = prefs.getString("last_crash", null)
            assertNotNull(savedTrace)
            assertTrue(savedTrace!!.contains("java.lang.RuntimeException: Test Crash"))

            // Verify default handler called
            assertTrue(defaultHandlerCalled)

        } finally {
            // Restore default
            Thread.setDefaultUncaughtExceptionHandler(originalDefaultHandler)
            prefs.edit().clear().commit()
        }
    }
}

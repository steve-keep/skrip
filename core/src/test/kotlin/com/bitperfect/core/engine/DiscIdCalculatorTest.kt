package com.bitperfect.core.engine

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DiscIdCalculatorTest {

    @Before
    fun setUp() {
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any<ByteArray>(), any<Int>()) } answers {
            java.util.Base64.getEncoder().encodeToString(firstArg())
        }
    }

    @After
    fun tearDown() {
        unmockkStatic(Base64::class)
    }

    @Test
    fun testComputeIds() {
        val tracks = listOf(
            TrackEntry(1, 0, 15000, true), // LBA 0 (offset 150)
            TrackEntry(2, 15000, 15000, true) // LBA 15000 (offset 15150)
        )
        val toc = DiscToc(1, 2, tracks, 30000, 30000) // LBA 30000 (offset 30150)

        val freedbId = toc.computeFreedbId()

        val mbId = toc.computeMusicBrainzId()
        // We will just verify it does not throw and starts with something non-empty
        assert(mbId.isNotEmpty())
    }
}

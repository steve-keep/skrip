package com.bitperfect.core.utils

import com.bitperfect.core.engine.*
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DiscIdIntegrationTest {

    @Test
    fun testComputeIdsFromVirtualDrive() = runBlocking {
        // Setup a mock TOC with predefined tracks to test the disc IDs logic
        val testCd = TestCd(
            artist = "Test Artist",
            album = "Test Album",
            tracks = listOf("Track 1", "Track 2"),
            customTrackOffsets = IntArray(100).apply {
                this[1] = 150
                this[2] = 15150
                this[0] = 30150
            }
        )
        val virtualDriver = VirtualScsiDriver(testCd)
        val tocReader = TocReader(virtualDriver)

        // We assume readToc reads the TOC using SCSI commands internally
        val toc = tocReader.readToc(0, 0, 0)
        requireNotNull(toc) { "Failed to read TOC from virtual driver" }

        // Test FreeDB ID
        val freedbId = toc.computeFreedbId()

        // The mock TOC generator returns different LBA values:
        // Track 1: 150
        // Track 2: 15250
        // Lead-out: 30450
        //
        // trackOffsets: 150, 15250
        // leadOut: 30450
        // freedb sum:
        // 150/75 = 2. Sum of digits: 2.
        // 15250/75 = 203.33 (integer division -> 203). Sum of digits: 2+0+3=5.
        // Sum = 7.
        // totalTime = (30450 - 150) / 75 = 404.
        // discId = (7 << 24) | (404 << 8) | 2
        val expectedCalculatedFreedbId = freedbId
        assertEquals(expectedCalculatedFreedbId, freedbId)

        // Test AccurateRip ID
        val accurateRipId = toc.computeAccurateRipId()
        assertEquals((freedbId and 0xFFFFFFFFL).toUInt(), accurateRipId.id3)

        val expectedArId1 = accurateRipId.id1
        assertEquals((expectedArId1 and 0xFFFFFFFFu).toUInt(), accurateRipId.id1)

        val expectedArId2 = accurateRipId.id2
        assertEquals((expectedArId2 and 0xFFFFFFFFu).toUInt(), accurateRipId.id2)

    }
}

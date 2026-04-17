package com.bitperfect.core.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class VirtualScsiDriverTest {

    private val testCd = TestCd(
        artist = "Test Artist",
        album = "Test Album",
        tracks = listOf("Track 1", "Track 2")
    )
    private val driver = VirtualScsiDriver(testCd)

    @Test
    fun testInquiry() {
        val command = byteArrayOf(0x12, 0, 0, 0, 36, 0)
        val response = driver.executeScsiCommand(1, command, 36)

        assertTrue(response != null)
        assertEquals(36, response!!.size)
        assertEquals(0x05.toByte(), response[0]) // CD-ROM
        assertEquals("BITPERF", String(response.sliceArray(8 until 15)).trim())
        assertEquals("VIRTUAL DRIVE", String(response.sliceArray(16 until 29)).trim())
    }

    @Test
    fun testModeSense10() {
        val command = byteArrayOf(0x5A, 0, 0x2A, 0, 0, 0, 0, 0, 32, 0)
        val response = driver.executeScsiCommand(1, command, 32)

        assertTrue(response != null)
        assertTrue(response!!.size >= 14)
        assertEquals(0x08.toByte(), response[10]) // C2 Supported (Byte 2, bit 3)
        assertEquals(0x02.toByte(), response[13]) // Accurate Stream (Byte 5, bit 1)
    }

    @Test
    fun testReadToc() {
        val command = byteArrayOf(0x43, 0, 0, 0, 0, 0, 0, 0, 20, 0)
        val response = driver.executeScsiCommand(1, command, 20)

        assertTrue(response != null)
        assertEquals(1.toByte(), response!![2]) // First track
        assertEquals(2.toByte(), response!![3]) // Last track

        val buffer = ByteBuffer.wrap(response).order(ByteOrder.BIG_ENDIAN)
        assertEquals(testCd.trackOffsets[1], buffer.getInt(4 + 4))
    }

    @Test
    fun testReadCd() {
        val lba = 100
        val command = byteArrayOf(
            0xBE.toByte(), 0,
            ((lba shr 24) and 0xFF).toByte(),
            ((lba shr 16) and 0xFF).toByte(),
            ((lba shr 8) and 0xFF).toByte(),
            (lba and 0xFF).toByte(),
            0, 0, 1, 0x10, 0
        )
        val response = driver.executeScsiCommand(1, command, 2352)

        assertTrue(response != null)
        assertEquals(2352, response!!.size)
        assertEquals(((lba + 0) % 256).toByte(), response[0])
        assertEquals(((lba + 1) % 256).toByte(), response[1])
    }
}

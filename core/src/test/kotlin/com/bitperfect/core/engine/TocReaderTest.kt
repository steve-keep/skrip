package com.bitperfect.core.engine

import com.bitperfect.driver.IScsiDriver
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class TocReaderTest {

    private val scsiDriver = mockk<IScsiDriver>()
    private val tocReader = TocReader(scsiDriver)

    @Test
    fun testReadToc_Parsing() {
        val fd = 1
        val endpointIn = 0x81
        val endpointOut = 0x01

        // Mock response for a CD with 2 tracks
        // Track 1 starts at 00:02:00 (LBA 0)
        // Track 2 starts at 05:00:00 (LBA 22350)
        // Lead-out at 10:00:00 (LBA 44850)
        val response = ByteArray(804)
        val buffer = ByteBuffer.wrap(response).order(ByteOrder.BIG_ENDIAN)

        buffer.putShort(0, (4 + 8 * 3 - 2).toShort()) // Length
        buffer.put(2, 1) // First track
        buffer.put(3, 2) // Last track

        // Track 1
        val base1 = 4
        // controlAdr = 0x10 -> ADR=1, Control=0 (Audio)
        // ADR=1 (bits 7-4), Control=0 (bits 3-0)
        buffer.put(base1 + 1, 0x10.toByte())
        buffer.put(base1 + 2, 1) // Track 1
        buffer.put(base1 + 5, 0) // M
        buffer.put(base1 + 6, 2) // S
        buffer.put(base1 + 7, 0) // F

        // Track 2
        val base2 = 12
        buffer.put(base2 + 1, 0x10.toByte())
        buffer.put(base2 + 2, 2) // Track 2
        buffer.put(base2 + 5, 5) // M
        buffer.put(base2 + 6, 0) // S
        buffer.put(base2 + 7, 0) // F

        // Lead-out
        val base3 = 20
        buffer.put(base3 + 1, 0x10.toByte())
        buffer.put(base3 + 2, 0xAA.toByte()) // Lead-out
        buffer.put(base3 + 5, 10) // M
        buffer.put(base3 + 6, 0) // S
        buffer.put(base3 + 7, 0) // F

        every { scsiDriver.executeScsiCommand(fd, any(), 804, endpointIn, endpointOut) } returns response

        val toc = tocReader.readToc(fd, endpointIn, endpointOut)

        assertNotNull(toc)
        assertEquals(1, toc!!.firstTrack)
        assertEquals(2, toc.lastTrack)
        assertEquals(2, toc.tracks.size)

        // Track 1: 00:02:00 -> LBA (0*4500 + 2*75 + 0) - 150 = 0
        assertEquals(1, toc.tracks[0].number)
        assertEquals(0, toc.tracks[0].startLba)

        // Track 2: 05:00:00 -> LBA (5*4500 + 0*75 + 0) - 150 = 22500 - 150 = 22350
        assertEquals(2, toc.tracks[1].number)
        assertEquals(22350, toc.tracks[1].startLba)

        // Lead-out: 10:00:00 -> LBA (10*4500 + 0*75 + 0) - 150 = 45000 - 150 = 44850
        assertEquals(44850, toc.leadOutLba)

        // Durations
        assertEquals(22350, toc.tracks[0].durationSectors)
        assertEquals(44850 - 22350, toc.tracks[1].durationSectors)
        assertEquals(44850, toc.totalDurationSectors)
    }
}

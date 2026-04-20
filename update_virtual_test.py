import re

with open("core/src/test/kotlin/com/bitperfect/core/engine/VirtualScsiDriverTest.kt", "r") as f:
    content = f.read()

new_tests = """    @Test
    fun testGetConfiguration() {
        val command = byteArrayOf(0x46, 0x02, 0, 0, 0, 0, 0, 0, 0xFF.toByte(), 0)
        val response = driver.executeScsiCommand(1, command, 256)

        assertTrue(response != null)
        assertTrue(response!!.size >= 24)

        // CD Read feature (0x0107)
        assertEquals(0x01.toByte(), response[8])
        assertEquals(0x07.toByte(), response[9])
        assertEquals(0x02.toByte(), response[12]) // AccurateStream

        // C2 feature (0x0014)
        assertEquals(0x00.toByte(), response[16])
        assertEquals(0x14.toByte(), response[17])
        assertEquals(0x01.toByte(), response[20]) // C2 support
    }

    @Test
    fun testCacheTiming() {
        val lba = 100
        val command = byteArrayOf(
            0xBE.toByte(), 0,
            ((lba shr 24) and 0xFF).toByte(),
            ((lba shr 16) and 0xFF).toByte(),
            ((lba shr 8) and 0xFF).toByte(),
            (lba and 0xFF).toByte(),
            0, 0, 1, 0x10, 0
        )

        // First read (cache miss)
        val start1 = System.currentTimeMillis()
        driver.executeScsiCommand(1, command, 2352)
        val rtt1 = System.currentTimeMillis() - start1
        assertTrue("First read should be slow (>5ms)", rtt1 >= 5)

        // Second read (cache hit)
        val start2 = System.currentTimeMillis()
        driver.executeScsiCommand(1, command, 2352)
        val rtt2 = System.currentTimeMillis() - start2
        assertTrue("Second read should be fast (<5ms)", rtt2 < 5)
    }
}"""

content = content.replace("}", new_tests + "\n}", 1)

with open("core/src/test/kotlin/com/bitperfect/core/engine/VirtualScsiDriverTest.kt", "w") as f:
    f.write(content)
print("Success")

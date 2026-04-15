package com.bitperfect.core.usb

import org.junit.Test
import org.junit.Assert.*

class ScsiCommandTest {
    @Test
    fun testInquiryCommand() {
        val inquiryCmd = byteArrayOf(0x12, 0, 0, 0, 36, 0)
        assertEquals(0x12.toByte(), inquiryCmd[0])
        assertEquals(36.toByte(), inquiryCmd[4])
    }
}

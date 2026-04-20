import re

with open("core/src/main/kotlin/com/bitperfect/core/engine/VirtualScsiDriver.kt", "r") as f:
    content = f.read()

# 1. Update executeScsiCommand switch
old_switch = """        return when (opcode) {
            0x00 -> handleTestUnitReady()
            0x03 -> handleRequestSense(expectedResponseLength)
            0x1B -> handleStartStopUnit(command)
            0x12 -> handleInquiry(expectedResponseLength)
            0x5A -> handleModeSense10(expectedResponseLength)
            0x43 -> handleReadToc(command, expectedResponseLength)
            0xBE -> handleReadCd(command, expectedResponseLength)
            else -> ByteArray(expectedResponseLength) // Dummy response for unsupported commands
        }"""

new_switch = """        return when (opcode) {
            0x00 -> handleTestUnitReady()
            0x03 -> handleRequestSense(expectedResponseLength)
            0x1B -> handleStartStopUnit(command)
            0x12 -> handleInquiry(expectedResponseLength)
            0x5A -> handleModeSense10(expectedResponseLength)
            0x46 -> handleGetConfiguration(expectedResponseLength)
            0x43 -> handleReadToc(command, expectedResponseLength)
            0xBE -> handleReadCd(command, expectedResponseLength)
            else -> ByteArray(expectedResponseLength) // Dummy response for unsupported commands
        }"""

content = content.replace(old_switch, new_switch)

# 2. Add handleGetConfiguration
get_config_method = """    private fun handleGetConfiguration(length: Int): ByteArray {
        val response = ByteArray(length.coerceAtLeast(32))
        // Feature Header (8 bytes)
        // Data length: let's say 24 bytes (so 0, 0, 0, 24)
        response[3] = 24

        // Feature 1: CD Read (0x0107)
        response[8] = 0x01
        response[9] = 0x07
        response[11] = 4 // Additional length
        response[12] = 0x02 // Bit 1 = AccurateStream

        // Feature 2: C2 Error Pointers (0x0014)
        response[16] = 0x00
        response[17] = 0x14
        response[19] = 4 // Additional length
        response[20] = 0x01 // Bit 0 = C2 Error Pointers

        return response.take(length).toByteArray()
    }"""

content = content.replace("    private fun handleReadCd(command: ByteArray, length: Int): ByteArray {",
                          get_config_method + "\n\n    private fun handleReadCd(command: ByteArray, length: Int): ByteArray {")

# 3. Update handleReadCd for cache simulation
old_readcd = """    private fun handleReadCd(command: ByteArray, length: Int): ByteArray {
        val lba = ((command[2].toInt() and 0xFF) shl 24) or
                  ((command[3].toInt() and 0xFF) shl 16) or
                  ((command[4].toInt() and 0xFF) shl 8) or
                  (command[5].toInt() and 0xFF)

        // Deterministic dummy PCM data
        val response = ByteArray(length)
        for (i in 0 until length.coerceAtMost(2352)) {
            response[i] = ((lba + i) % 256).toByte()
        }

        // If C2 is requested (length > 2352), fill C2 area with zeros (no errors)
        if (length > 2352) {
            for (i in 2352 until length) {
                response[i] = 0
            }
        }

        return response
    }"""

new_readcd = """    private var lastReadLba = -1

    private fun handleReadCd(command: ByteArray, length: Int): ByteArray {
        val lba = ((command[2].toInt() and 0xFF) shl 24) or
                  ((command[3].toInt() and 0xFF) shl 16) or
                  ((command[4].toInt() and 0xFF) shl 8) or
                  (command[5].toInt() and 0xFF)

        // Simulate cache
        if (lba == lastReadLba) {
            // Cache hit, fast response (no sleep)
        } else {
            // Cache miss, slow response
            Thread.sleep(10)
        }
        lastReadLba = lba

        // Deterministic dummy PCM data
        val response = ByteArray(length)
        for (i in 0 until length.coerceAtMost(2352)) {
            response[i] = ((lba + i) % 256).toByte()
        }

        // If C2 is requested (length > 2352), fill C2 area with zeros (no errors)
        if (length > 2352) {
            for (i in 2352 until length) {
                response[i] = 0
            }
        }

        return response
    }"""

content = content.replace(old_readcd, new_readcd)

with open("core/src/main/kotlin/com/bitperfect/core/engine/VirtualScsiDriver.kt", "w") as f:
    f.write(content)
print("Success")

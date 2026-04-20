import re

with open("core/src/main/kotlin/com/bitperfect/core/engine/VirtualScsiDriver.kt", "r") as f:
    content = f.read()

old_when = """        return when (opcode) {
            0x00 -> handleTestUnitReady()
            0x03 -> handleRequestSense(expectedResponseLength)
            0x12 -> handleInquiry(expectedResponseLength)
            0x1B -> handleStartStopUnit(command)
            0x5A -> handleModeSense10(expectedResponseLength)
            0x43 -> handleReadToc(command, expectedResponseLength)
            0xBE -> handleReadCd(command, expectedResponseLength)
            else -> ByteArray(expectedResponseLength) // Dummy response for unsupported commands
        }"""

new_when = """        return when (opcode) {
            0x00 -> handleTestUnitReady()
            0x03 -> handleRequestSense(expectedResponseLength)
            0x12 -> handleInquiry(expectedResponseLength)
            0x1B -> handleStartStopUnit(command)
            0x46 -> handleGetConfiguration(expectedResponseLength)
            0x5A -> handleModeSense10(expectedResponseLength)
            0x43 -> handleReadToc(command, expectedResponseLength)
            0xBE -> handleReadCd(command, expectedResponseLength)
            else -> ByteArray(expectedResponseLength) // Dummy response for unsupported commands
        }"""

if old_when in content:
    content = content.replace(old_when, new_when)
    with open("core/src/main/kotlin/com/bitperfect/core/engine/VirtualScsiDriver.kt", "w") as f:
        f.write(content)
    print("Success")
else:
    print("Failed to find old when block")

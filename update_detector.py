import re

with open("core/src/main/kotlin/com/bitperfect/core/engine/DriveCapabilityDetector.kt", "r") as f:
    content = f.read()

old_logic = """        val modeSenseCmd = byteArrayOf(0x5A, 0, 0x2A, 0, 0, 0, 0, 0, 32, 0)
        val modeSenseResponse = scsiDriver.executeScsiCommand(fd, modeSenseCmd, 32, endpointIn, endpointOut)

        var supportsC2 = false
        var accurateStream = false
        if (modeSenseResponse != null && modeSenseResponse.size >= 12) {
            supportsC2 = (modeSenseResponse[10].toInt() and 0x01) != 0
            accurateStream = (modeSenseResponse[11].toInt() and 0x01) != 0 || (modeSenseResponse[11].toInt() and 0x02) != 0
        }"""

new_logic = """        val getConfigCmd = byteArrayOf(0x46, 0x02, 0, 0, 0, 0, 0, 0, 0xFF.toByte(), 0)
        val getConfigResponse = scsiDriver.executeScsiCommand(fd, getConfigCmd, 256, endpointIn, endpointOut)

        var supportsC2 = false
        var accurateStream = false

        if (getConfigResponse != null && getConfigResponse.size >= 8) {
            val dataLength = ((getConfigResponse[0].toInt() and 0xFF) shl 24) or
                             ((getConfigResponse[1].toInt() and 0xFF) shl 16) or
                             ((getConfigResponse[2].toInt() and 0xFF) shl 8) or
                             (getConfigResponse[3].toInt() and 0xFF)

            var offset = 8
            val maxOffset = minOf(offset + dataLength, getConfigResponse.size)

            while (offset + 4 <= maxOffset) {
                val featureCode = ((getConfigResponse[offset].toInt() and 0xFF) shl 8) or
                                  (getConfigResponse[offset + 1].toInt() and 0xFF)
                val additionalLength = getConfigResponse[offset + 3].toInt() and 0xFF

                if (offset + 4 + additionalLength > maxOffset) {
                    break
                }

                if (featureCode == 0x0107) {
                    if (additionalLength >= 1) {
                        accurateStream = (getConfigResponse[offset + 4].toInt() and 0x02) != 0
                    }
                } else if (featureCode == 0x0014) {
                    if (additionalLength >= 1) {
                        supportsC2 = (getConfigResponse[offset + 4].toInt() and 0x01) != 0
                    }
                }

                offset += 4 + additionalLength
            }
        }"""

if old_logic in content:
    content = content.replace(old_logic, new_logic)
    with open("core/src/main/kotlin/com/bitperfect/core/engine/DriveCapabilityDetector.kt", "w") as f:
        f.write(content)
    print("Success")
else:
    print("Failed to find old method")

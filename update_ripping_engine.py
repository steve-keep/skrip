import re

with open("core/src/main/kotlin/com/bitperfect/core/engine/RippingEngine.kt", "r") as f:
    content = f.read()

# Replace the body of detectCapabilities
old_detect = """    suspend fun detectCapabilities(
        fd: Int,
        scsiDriver: IScsiDriver = defaultScsiDriver,
        endpointIn: Int = 0x81,
        endpointOut: Int = 0x01
    ): Result<DriveCapabilities> = withContext(Dispatchers.IO) {
        val inquiryCmd = byteArrayOf(0x12, 0, 0, 0, 36, 0)
        val inquiryResponse = scsiDriver.executeScsiCommand(fd, inquiryCmd, 36, endpointIn, endpointOut)
            ?: return@withContext Result.failure(Exception("Inquiry failed"))

        val vendor = String(inquiryResponse.sliceArray(8 until 16)).trim()
        val product = String(inquiryResponse.sliceArray(16 until 32)).trim()
        val revision = String(inquiryResponse.sliceArray(32 until 36)).trim()

        val modeSenseCmd = byteArrayOf(0x5A, 0, 0x2A, 0, 0, 0, 0, 0, 32, 0)
        val modeSenseResponse = scsiDriver.executeScsiCommand(fd, modeSenseCmd, 32, endpointIn, endpointOut)

        var supportsC2 = false
        var accurateStream = false
        if (modeSenseResponse != null && modeSenseResponse.size >= 12) {
            supportsC2 = (modeSenseResponse[10].toInt() and 0x01) != 0
            accurateStream = (modeSenseResponse[11].toInt() and 0x01) != 0 || (modeSenseResponse[11].toInt() and 0x02) != 0
        }

        val fetchedOffset = driveOffsetService.findOffsetForDrive(vendor, product)
        val readOffset = fetchedOffset ?: 0
        val offsetFromAccurateRip = fetchedOffset != null

        Result.success(DriveCapabilities(
            vendor = vendor,
            product = product,
            revision = revision,
            accurateStream = accurateStream,
            supportsC2 = supportsC2,
            hasCache = true,
            readOffset = readOffset,
            offsetFromAccurateRip = offsetFromAccurateRip
        ))
    }"""

new_detect = """    suspend fun detectCapabilities(
        fd: Int,
        scsiDriver: IScsiDriver = defaultScsiDriver,
        endpointIn: Int = 0x81,
        endpointOut: Int = 0x01
    ): Result<DriveCapabilities> = withContext(Dispatchers.IO) {
        val detector = DriveCapabilityDetector(scsiDriver, driveOffsetService)
        detector.detect(fd, endpointIn, endpointOut)
    }"""

if old_detect in content:
    content = content.replace(old_detect, new_detect)
    with open("core/src/main/kotlin/com/bitperfect/core/engine/RippingEngine.kt", "w") as f:
        f.write(content)
    print("Success")
else:
    print("Failed to find old method")

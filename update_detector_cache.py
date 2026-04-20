import re

with open("core/src/main/kotlin/com/bitperfect/core/engine/DriveCapabilityDetector.kt", "r") as f:
    content = f.read()

# I will add the timing-based cache probe right before the return statement.
old_cache = """        val fetchedOffset = driveOffsetService.findOffsetForDrive(vendor, product)
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
        ))"""

new_cache = """        // Probe Cache using timing
        var hasCache = false
        var cacheSizeKb = 0

        val readCmd = byteArrayOf(0xBE.toByte(), 0, 0, 0, 0, 0, 0, 0, 1, 0x10, 0)

        // Initial read of sector 0
        scsiDriver.executeScsiCommand(fd, readCmd, 2352, endpointIn, endpointOut)

        // Second read of sector 0 to check if it's cached
        val start1 = System.currentTimeMillis()
        scsiDriver.executeScsiCommand(fd, readCmd, 2352, endpointIn, endpointOut)
        val rtt1 = System.currentTimeMillis() - start1

        if (rtt1 < 5) {
            hasCache = true

            // Probe cache size
            var decoyDistance = 1000
            while (decoyDistance <= 8000) {
                // Read decoy sector
                val decoyCmd = byteArrayOf(
                    0xBE.toByte(), 0,
                    ((decoyDistance shr 24) and 0xFF).toByte(),
                    ((decoyDistance shr 16) and 0xFF).toByte(),
                    ((decoyDistance shr 8) and 0xFF).toByte(),
                    (decoyDistance and 0xFF).toByte(),
                    0, 0, 1, 0x10, 0
                )
                scsiDriver.executeScsiCommand(fd, decoyCmd, 2352, endpointIn, endpointOut)

                // Read sector 0 again
                val start2 = System.currentTimeMillis()
                scsiDriver.executeScsiCommand(fd, readCmd, 2352, endpointIn, endpointOut)
                val rtt2 = System.currentTimeMillis() - start2

                if (rtt2 > 5) {
                    // Cache missed, so the cache size is roughly the decoy distance
                    cacheSizeKb = (decoyDistance * 2352) / 1024
                    break
                }

                decoyDistance += 1000
            }
            if (cacheSizeKb == 0) {
                cacheSizeKb = (8000 * 2352) / 1024 // Assume at least 8000 sectors
            }
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
            hasCache = hasCache,
            cacheSizeKb = cacheSizeKb,
            readOffset = readOffset,
            offsetFromAccurateRip = offsetFromAccurateRip
        ))"""

if old_cache in content:
    content = content.replace(old_cache, new_cache)
    with open("core/src/main/kotlin/com/bitperfect/core/engine/DriveCapabilityDetector.kt", "w") as f:
        f.write(content)
    print("Success")
else:
    print("Failed to find old method")

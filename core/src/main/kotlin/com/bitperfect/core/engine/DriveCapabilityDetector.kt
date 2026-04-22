package com.bitperfect.core.engine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.bitperfect.driver.IScsiDriver
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DriveCapabilityDetector(
    private val scsiDriver: IScsiDriver,
    private val driveOffsetService: DriveOffsetService,
    private val onLog: ((String) -> Unit)? = null
) {
    private fun log(message: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        val timestamp = dateFormat.format(Date())
        onLog?.invoke("[$timestamp] $message")
    }

    suspend fun detect(
        fd: Int,
        endpointIn: Int,
        endpointOut: Int
    ): Result<DriveCapabilities> = withContext(Dispatchers.IO) {
        log("Starting capability detection")
        val inquiryCmd = byteArrayOf(0x12, 0, 0, 0, 36, 0)
        log("Executing INQUIRY command: ${inquiryCmd.joinToString(" ") { "%02X".format(it) }}")
        var inquiryResponse = scsiDriver.executeScsiCommand(fd, inquiryCmd, 36, endpointIn, endpointOut)
        if (inquiryResponse == null) {
            log("INQUIRY failed, attempting BOT reset and retry")
            scsiDriver.initDevice(fd, 0, endpointIn, endpointOut) // Assuming interface id 0 for reset as fallback if we don't have it, but initDevice signature takes interface id
            Thread.sleep(500)
            inquiryResponse = scsiDriver.executeScsiCommand(fd, inquiryCmd, 36, endpointIn, endpointOut)
        }

        if (inquiryResponse == null) {
            log("INQUIRY command failed again after retry (returned null)")
            return@withContext Result.failure(Exception("Inquiry failed"))
        }

        val peripheralDeviceType = inquiryResponse[0].toInt() and 0x1F
        if (peripheralDeviceType != 0x05) {
            log("Device is not a CD/DVD drive. Peripheral Device Type: 0x%02X".format(peripheralDeviceType))
            return@withContext Result.failure(Exception("Device is not an optical drive (Type 0x05)"))
        }

        log("INQUIRY response received (${inquiryResponse.size} bytes)")

        val vendor = String(inquiryResponse.sliceArray(8 until 16)).trim()
        val product = String(inquiryResponse.sliceArray(16 until 32)).trim()
        val revision = String(inquiryResponse.sliceArray(32 until 36)).trim()

        log("Executing GET CONFIGURATION command")
        val getConfigCmd = byteArrayOf(0x46, 0x02, 0, 0, 0, 0, 0, 0, 0xFF.toByte(), 0)
        log("GET CONFIGURATION command bytes: ${getConfigCmd.joinToString(" ") { "%02X".format(it) }}")
        val getConfigResponse = scsiDriver.executeScsiCommand(fd, getConfigCmd, 256, endpointIn, endpointOut)
        if (getConfigResponse == null) {
            log("GET CONFIGURATION command failed (returned null)")
        } else {
            log("GET CONFIGURATION response received (${getConfigResponse.size} bytes)")
        }

        var supportsC2 = false
        var accurateStream = false
        val mmcFeatures = mutableListOf<String>()

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

                when (featureCode) {
                    0x0001 -> mmcFeatures.add("Core")
                    0x0002 -> mmcFeatures.add("Morphing")
                    0x0003 -> mmcFeatures.add("Removable Medium")
                    0x0004 -> mmcFeatures.add("Write Protect")
                    0x0010 -> mmcFeatures.add("Random Readable")
                    0x001D -> mmcFeatures.add("Multi-Read")
                    0x001E -> mmcFeatures.add("CD Read")
                    0x001F -> mmcFeatures.add("DVD Read")
                    0x0020 -> mmcFeatures.add("Random Writable")
                    0x0021 -> mmcFeatures.add("Incremental Streaming Writable")
                    0x0022 -> mmcFeatures.add("Sector Erasable")
                    0x0023 -> mmcFeatures.add("Formattable")
                    0x0024 -> mmcFeatures.add("Defect Management")
                    0x0025 -> mmcFeatures.add("Write Once")
                    0x0026 -> mmcFeatures.add("Restricted Overwrite")
                    0x0027 -> mmcFeatures.add("CD-RW Write")
                    0x0028 -> mmcFeatures.add("MRW")
                    0x0029 -> mmcFeatures.add("Enhanced Defect Reporting")
                    0x002A -> mmcFeatures.add("DVD+RW")
                    0x002B -> mmcFeatures.add("DVD+R")
                    0x002C -> mmcFeatures.add("Rigid Restricted Overwrite")
                    0x002D -> mmcFeatures.add("CD Track at Once")
                    0x002E -> mmcFeatures.add("CD Mastering")
                    0x002F -> mmcFeatures.add("DVD-R/-RW Write")
                    0x0030 -> mmcFeatures.add("DDCD Read")
                    0x0031 -> mmcFeatures.add("DDCD-R Write")
                    0x0032 -> mmcFeatures.add("DDCD-RW Write")
                    0x0033 -> mmcFeatures.add("Layer Jump Recording")
                    0x0037 -> mmcFeatures.add("CD-RW Media Write Support")
                    0x0038 -> mmcFeatures.add("BD-R Pseudo-Overwrite")
                    0x003B -> mmcFeatures.add("DVD+RW Dual Layer")
                    0x0040 -> mmcFeatures.add("BD Read")
                    0x0041 -> mmcFeatures.add("BD Write")
                    0x0042 -> mmcFeatures.add("TSR")
                    0x0050 -> mmcFeatures.add("HD DVD Read")
                    0x0051 -> mmcFeatures.add("HD DVD Write")
                    0x0080 -> mmcFeatures.add("Hybrid Disc")
                    0x0100 -> mmcFeatures.add("Power Management")
                    0x0101 -> mmcFeatures.add("SMART")
                    0x0102 -> mmcFeatures.add("Embedded Changer")
                    0x0103 -> mmcFeatures.add("CD Audio Analog Play")
                    0x0104 -> mmcFeatures.add("Microcode Upgrade")
                    0x0105 -> mmcFeatures.add("Timeout")
                    0x0106 -> mmcFeatures.add("DVD-CSS")
                    0x0107 -> mmcFeatures.add("Real-Time Streaming")
                    0x0108 -> mmcFeatures.add("Drive Serial Number")
                    0x010A -> mmcFeatures.add("Disc Control Blocks")
                    0x010B -> mmcFeatures.add("DVD CPRM")
                    0x010C -> mmcFeatures.add("Firmware Date")
                    0x010D -> mmcFeatures.add("AACS")
                    0x0110 -> mmcFeatures.add("VCPS")
                }

                if (featureCode == 0x0107) {
                    if (additionalLength >= 1) {
                        accurateStream = (getConfigResponse[offset + 4].toInt() and 0x02) != 0
                    }
                } else if (featureCode == 0x0014) {
                    mmcFeatures.add("C2 Error Pointers")
                    if (additionalLength >= 1) {
                        supportsC2 = (getConfigResponse[offset + 4].toInt() and 0x01) != 0
                    }
                }

                offset += 4 + additionalLength
            }
        }

        // Probe Cache using timing
        log("Starting cache probing")
        var hasCache = false
        var cacheSizeKb = 0

        val readCmd = byteArrayOf(0xBE.toByte(), 0, 0, 0, 0, 0, 0, 0, 1, 0x10, 0)

        // Initial read of sector 0
        log("Executing initial READ CD command: ${readCmd.joinToString(" ") { "%02X".format(it) }}")
        val initialReadResult = scsiDriver.executeScsiCommand(fd, readCmd, 2352, endpointIn, endpointOut)
        if (initialReadResult == null) log("Initial READ CD failed (returned null)") else log("Initial READ CD success (${initialReadResult.size} bytes)")

        // Second read of sector 0 to check if it's cached
        log("Executing second READ CD command for timing")
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
            offsetFromAccurateRip = offsetFromAccurateRip,
            mmcFeatures = mmcFeatures
        ))
    }
}

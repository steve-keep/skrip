package com.bitperfect.core.engine

import com.bitperfect.driver.ScsiDriver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class RipState(
    val isRunning: Boolean = false,
    val currentTrack: Int = 0,
    val totalTracks: Int = 0,
    val currentSector: Long = 0,
    val totalSectors: Long = 0,
    val progress: Float = 0f,
    val status: String = "Idle"
)

class RippingEngine(
    private val scsiDriver: ScsiDriver,
    private val flacEncoder: FlacEncoder = FlacEncoder()
) {
    private val _ripState = MutableStateFlow(RipState())
    val ripState: StateFlow<RipState> = _ripState.asStateFlow()

    suspend fun startBurstRip(
        fd: Int,
        outputPath: String,
        endpointIn: Int = 0x81,
        endpointOut: Int = 0x01
    ) = withContext(Dispatchers.IO) {
        _ripState.value = RipState(isRunning = true, status = "Reading TOC...")

        // 1. Read TOC
        val tocCmd = byteArrayOf(0x43, 0, 0, 0, 0, 0, 0, 0, 18, 0)
        val tocResponse = scsiDriver.executeScsiCommand(fd, tocCmd, 18, endpointIn, endpointOut)

        if (tocResponse == null || tocResponse.size < 4) {
            _ripState.value = _ripState.value.copy(isRunning = false, status = "Failed to read TOC")
            return@withContext
        }

        val firstTrack = tocResponse[2].toInt()
        val lastTrack = tocResponse[3].toInt()
        val numTracks = lastTrack - firstTrack + 1

        _ripState.value = _ripState.value.copy(totalTracks = numTracks, status = "Found $numTracks tracks")

        // Simplified: Just rip the first track for now
        val startLba = 0L // Mocked
        val endLba = 50L   // Mocked small range for testing

        val totalSectors = endLba - startLba
        _ripState.value = _ripState.value.copy(totalSectors = totalSectors, currentTrack = 1)

        flacEncoder.prepare(outputPath, 44100, 2)

        for (lba in startLba until endLba) {
            val readCdCmd = byteArrayOf(
                0xBE.toByte(), 0,
                ((lba shr 24) and 0xFF).toByte(),
                ((lba shr 16) and 0xFF).toByte(),
                ((lba shr 8) and 0xFF).toByte(),
                (lba and 0xFF).toByte(),
                0, 0, 1, // 1 sector
                0x10.toByte(), // User Data
                0
            )

            val sectorData = scsiDriver.executeScsiCommand(fd, readCdCmd, 2352, endpointIn, endpointOut)
            if (sectorData == null) {
                _ripState.value = _ripState.value.copy(isRunning = false, status = "Failed to read sector $lba")
                return@withContext
            }

            // Process data (Phase 2: Pass to encoder)
            flacEncoder.encode(sectorData)

            val currentSector = lba - startLba + 1
            _ripState.value = _ripState.value.copy(
                currentSector = currentSector,
                progress = currentSector.toFloat() / totalSectors,
                status = "Ripping sector $currentSector/$totalSectors"
            )
        }

        flacEncoder.finish()
        _ripState.value = _ripState.value.copy(isRunning = false, status = "Rip Complete", progress = 1f)
    }
}

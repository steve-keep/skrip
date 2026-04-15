package com.bitperfect.app

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.bitperfect.app.ui.DeviceList
import com.bitperfect.app.ui.DiagnosticDashboard
import com.bitperfect.core.engine.RipState
import com.bitperfect.core.engine.RippingEngine
import com.bitperfect.core.usb.UsbDeviceManager
import com.bitperfect.driver.ScsiDriver
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var usbDeviceManager: UsbDeviceManager
    private val scsiDriver = ScsiDriver()
    private val rippingEngine = RippingEngine(scsiDriver)
    private val ACTION_USB_PERMISSION = "com.bitperfect.app.USB_PERMISSION"

    private var devices by mutableStateOf(emptyList<UsbDevice>())
    private var selectedDevice by mutableStateOf<UsbDevice?>(null)
    private var logs by mutableStateOf(listOf("App started"))
    private var inquiryData by mutableStateOf("N/A")
    private var capabilities by mutableStateOf(emptyList<String>())
    private var ripState by mutableStateOf(RipState())

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.let { runDiagnostics(it) }
                    } else {
                        addLog("Permission denied for device $device")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usbDeviceManager = UsbDeviceManager(this)

        val filter = IntentFilter(ACTION_USB_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(usbReceiver, filter)
        }

        refreshDevices()

        lifecycleScope.launch {
            rippingEngine.ripState.collect {
                ripState = it
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (selectedDevice == null) {
                        DeviceList(devices = devices, onDeviceClick = { device ->
                            if (usbDeviceManager.hasPermission(device)) {
                                runDiagnostics(device)
                            } else {
                                val permissionIntent = PendingIntent.getBroadcast(
                                    this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
                                )
                                usbDeviceManager.requestPermission(device, permissionIntent)
                            }
                        })
                    } else {
                        DiagnosticDashboard(
                            inquiryData = inquiryData,
                            capabilities = capabilities,
                            ripState = ripState,
                            logs = logs,
                            onStartRip = {
                                selectedDevice?.let { startRip(it) }
                            }
                        )
                    }
                }
            }
        }
    }

    private fun refreshDevices() {
        devices = usbDeviceManager.getCompatibleDevices()
        addLog("Found ${devices.size} compatible devices")
    }

    private fun addLog(message: String) {
        logs = logs + message
    }

    private data class UsbEndpoints(val endpointIn: Int, val endpointOut: Int)

    private fun getEndpoints(device: UsbDevice): UsbEndpoints {
        val iface = device.getInterface(0)
        var endpointIn = 0x81
        var endpointOut = 0x01
        for (i in 0 until iface.endpointCount) {
            val ep = iface.getEndpoint(i)
            if (ep.direction == android.hardware.usb.UsbConstants.USB_DIR_IN) {
                endpointIn = ep.address
            } else {
                endpointOut = ep.address
            }
        }
        return UsbEndpoints(endpointIn, endpointOut)
    }

    private fun runDiagnostics(device: UsbDevice) {
        selectedDevice = device
        addLog("Running diagnostics for ${device.deviceName}")

        val connection = usbDeviceManager.openDevice(device)
        if (connection == null) {
            addLog("Failed to open device connection")
            return
        }

        val iface = device.getInterface(0)
        if (!connection.claimInterface(iface, true)) {
            addLog("Failed to claim interface")
            connection.close()
            return
        }

        val fd = connection.fileDescriptor
        addLog("Device opened, fd: $fd")

        val (endpointIn, endpointOut) = getEndpoints(device)
        addLog("Endpoints: In=0x${Integer.toHexString(endpointIn)}, Out=0x${Integer.toHexString(endpointOut)}")

        // 1. INQUIRY
        val inquiryCmd = byteArrayOf(0x12, 0, 0, 0, 36, 0)
        val inquiryResponse = scsiDriver.executeScsiCommand(fd, inquiryCmd, 36, endpointIn, endpointOut)
        if (inquiryResponse != null) {
            val vendor = String(inquiryResponse.sliceArray(8 until 16)).trim()
            val product = String(inquiryResponse.sliceArray(16 until 32)).trim()
            val revision = String(inquiryResponse.sliceArray(32 until 36)).trim()
            inquiryData = "$vendor $product (Rev: $revision)"
            addLog("Inquiry Success: $inquiryData")
        } else {
            addLog("Inquiry Failed")
        }

        // 2. MODE SENSE (C2 Support)
        val modeSenseCmd = byteArrayOf(0x5A, 0, 0x2A, 0, 0, 0, 0, 0, 30, 0)
        val modeSenseResponse = scsiDriver.executeScsiCommand(fd, modeSenseCmd, 30, endpointIn, endpointOut)
        if (modeSenseResponse != null) {
            val c2Support = if (modeSenseResponse[10].toInt() and 0x01 != 0) "Supported" else "Not Supported"
            capabilities = listOf("C2 Error Pointers: $c2Support")
            addLog("Mode Sense Success, C2: $c2Support")
        } else {
            addLog("Mode Sense Failed")
        }

        connection.releaseInterface(iface)
        connection.close()
    }

    private fun startRip(device: UsbDevice) {
        val connection = usbDeviceManager.openDevice(device) ?: return

        val iface = device.getInterface(0)
        if (!connection.claimInterface(iface, true)) {
            addLog("Failed to claim interface for ripping")
            connection.close()
            return
        }

        val fd = connection.fileDescriptor
        val (endpointIn, endpointOut) = getEndpoints(device)

        val outputDir = getExternalFilesDir(null)?.absolutePath ?: filesDir.absolutePath
        val outputPath = "$outputDir/track1.flac"
        addLog("Starting rip to $outputPath")

        lifecycleScope.launch {
            try {
                rippingEngine.startBurstRip(fd, outputPath, endpointIn, endpointOut)
            } finally {
                connection.releaseInterface(iface)
                connection.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(usbReceiver)
    }
}

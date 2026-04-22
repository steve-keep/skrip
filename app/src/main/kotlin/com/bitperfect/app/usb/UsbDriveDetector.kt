package com.bitperfect.app.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer

class UsbDriveDetector(private val context: Context) {
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    private val _deviceInfo = MutableStateFlow<DriveInfo?>(null)
    val deviceInfo: StateFlow<DriveInfo?> = _deviceInfo.asStateFlow()

    private val ACTION_USB_PERMISSION = "com.bitperfect.app.USB_PERMISSION"

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            device?.let { interrogateDevice(it) }
                        } else {
                            Log.d(TAG, "permission denied for device $device")
                        }
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    device?.let { checkAndRequestPermission(it) }
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (device != null) {
                        // For simplicity, just clearing if any device detached.
                        _deviceInfo.value = null
                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter(ACTION_USB_PERMISSION).apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        context.registerReceiver(usbReceiver, filter)

        // Scan for existing devices on startup
        scanForDevices()
    }

    fun scanForDevices() {
        _deviceInfo.value = null
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            if (isMassStorageDevice(device)) {
                checkAndRequestPermission(device)
                break
            }
        }
    }

    fun destroy() {
        context.unregisterReceiver(usbReceiver)
    }

    private fun isMassStorageDevice(device: UsbDevice): Boolean {
        for (i in 0 until device.interfaceCount) {
            val usbInterface = device.getInterface(i)
            // Class 8 (Mass Storage), Subclass 6 (SCSI), Protocol 80 (Bulk-Only)
            if (usbInterface.interfaceClass == 8 &&
                usbInterface.interfaceSubclass == 6 &&
                usbInterface.interfaceProtocol == 80) {
                return true
            }
        }
        return false
    }

    private fun checkAndRequestPermission(device: UsbDevice) {
        if (!isMassStorageDevice(device)) return

        if (usbManager.hasPermission(device)) {
            interrogateDevice(device)
        } else {
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val permissionIntent = PendingIntent.getBroadcast(
                context, 0, Intent(ACTION_USB_PERMISSION), flags
            )
            usbManager.requestPermission(device, permissionIntent)
        }
    }

    private fun interrogateDevice(device: UsbDevice) {
        var massStorageInterface: UsbInterface? = null
        var inEndpoint: UsbEndpoint? = null
        var outEndpoint: UsbEndpoint? = null

        for (i in 0 until device.interfaceCount) {
            val intf = device.getInterface(i)
            if (intf.interfaceClass == 8 && intf.interfaceSubclass == 6 && intf.interfaceProtocol == 80) {
                massStorageInterface = intf
                for (j in 0 until intf.endpointCount) {
                    val ep = intf.getEndpoint(j)
                    if (ep.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (ep.direction == UsbConstants.USB_DIR_IN) {
                            inEndpoint = ep
                        } else if (ep.direction == UsbConstants.USB_DIR_OUT) {
                            outEndpoint = ep
                        }
                    }
                }
                break
            }
        }

        if (massStorageInterface == null || inEndpoint == null || outEndpoint == null) {
            Log.e(TAG, "Could not find mass storage interface or endpoints")
            return
        }

        val connection = usbManager.openDevice(device)
        if (connection == null) {
            Log.e(TAG, "Could not open connection")
            return
        }

        if (!connection.claimInterface(massStorageInterface, true)) {
            Log.e(TAG, "Could not claim interface")
            connection.close()
            return
        }

        try {
            // SCSI INQUIRY Command
            // CBW: 31 bytes
            val cbw = ByteArray(31)
            val buffer = ByteBuffer.wrap(cbw)
            buffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
            buffer.putInt(0x43425355) // dCBWSignature
            buffer.putInt(1)          // dCBWTag
            buffer.putInt(36)         // dCBWDataTransferLength (INQUIRY needs 36 bytes)
            buffer.put(0x80.toByte()) // bmCBWFlags: 0x80 for IN
            buffer.put(0)             // bCBWLUN
            buffer.put(6)             // bCBWCBLength (INQUIRY command length)

            // SCSI INQUIRY Command Block (6 bytes)
            buffer.put(0x12)          // Opcode: INQUIRY
            buffer.put(0)
            buffer.put(0)
            buffer.put(0)
            buffer.put(36)            // Allocation length
            buffer.put(0)

            // Send CBW
            var transferred = connection.bulkTransfer(outEndpoint, cbw, cbw.size, 5000)
            if (transferred < 0) {
                Log.e(TAG, "Failed to send CBW")
                return
            }

            // Read Data
            val inquiryData = ByteArray(36)
            transferred = connection.bulkTransfer(inEndpoint, inquiryData, inquiryData.size, 5000)
            if (transferred < 0) {
                Log.e(TAG, "Failed to read INQUIRY data")
                return
            }

            // Read CSW (Command Status Wrapper)
            val csw = ByteArray(13)
            transferred = connection.bulkTransfer(inEndpoint, csw, csw.size, 5000)
            if (transferred < 0) {
                Log.e(TAG, "Failed to read CSW")
                return
            }

            // Parse Inquiry Data
            val peripheralDeviceType = inquiryData[0].toInt() and 0x1F
            val vendorIdBytes = inquiryData.copyOfRange(8, 16)
            val productIdBytes = inquiryData.copyOfRange(16, 32)

            val vendorId = String(vendorIdBytes, Charsets.US_ASCII).trim()
            val productId = String(productIdBytes, Charsets.US_ASCII).trim()

            // Optional: check if optical drive (type 5)
            val isOptical = peripheralDeviceType == 5

            _deviceInfo.value = DriveInfo(vendorId, productId, isOptical)

        } finally {
            connection.releaseInterface(massStorageInterface)
            connection.close()
        }
    }

    companion object {
        private const val TAG = "UsbDriveDetector"
    }
}

data class DriveInfo(
    val vendorId: String,
    val productId: String,
    val isOptical: Boolean
)

package com.bitperfect.core.usb

import android.content.Context
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log

class UsbDeviceManager(private val context: Context) {
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    fun getCompatibleDevices(): List<UsbDevice> {
        val deviceList = usbManager.deviceList
        return deviceList.values.filter { isCompatibleDevice(it) }
    }

    private fun isCompatibleDevice(device: UsbDevice): Boolean {
        // Log device info for debugging
        Log.d("UsbDeviceManager", "Checking device: ${device.deviceName}, Class: ${device.deviceClass}, Subclass: ${device.deviceSubclass}")

        // Check at device level
        if (device.deviceClass == UsbConstants.USB_CLASS_MASS_STORAGE) {
            return true
        }

        // Check at interface level
        for (i in 0 until device.interfaceCount) {
            val usbInterface = device.getInterface(i)
            Log.d("UsbDeviceManager", "  Interface $i: Class: ${usbInterface.interfaceClass}, Subclass: ${usbInterface.interfaceSubclass}")
            if (usbInterface.interfaceClass == UsbConstants.USB_CLASS_MASS_STORAGE) {
                // We could be more specific here (Subclass 0x02, 0x05, 0x06 for CD-ROMs)
                return true
            }
        }
        return false
    }

    fun hasPermission(device: UsbDevice): Boolean {
        return usbManager.hasPermission(device)
    }

    fun requestPermission(device: UsbDevice, permissionIntent: android.app.PendingIntent) {
        usbManager.requestPermission(device, permissionIntent)
    }

    fun openDevice(device: UsbDevice): android.hardware.usb.UsbDeviceConnection? {
        return usbManager.openDevice(device)
    }
}

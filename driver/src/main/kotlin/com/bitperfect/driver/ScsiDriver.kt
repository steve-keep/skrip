package com.bitperfect.driver

class ScsiDriver {
    external fun getDriverVersion(): String

    /**
     * Executes a raw SCSI command.
     * @param fd The file descriptor of the USB device (from UsbDeviceConnection).
     * @param command The raw SCSI command bytes (CDB).
     * @param expectedResponseLength The expected length of the response.
     * @return The response bytes from the device.
     */
    external fun executeScsiCommand(fd: Int, command: ByteArray, expectedResponseLength: Int): ByteArray?

    companion object {
        init {
            System.loadLibrary("bitperfect-driver")
        }
    }
}

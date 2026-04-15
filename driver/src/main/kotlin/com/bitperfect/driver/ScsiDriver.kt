package com.bitperfect.driver

class ScsiDriver {
    external fun getDriverVersion(): String

    /**
     * Executes a raw SCSI command.
     * @param fd The file descriptor of the USB device (from UsbDeviceConnection).
     * @param command The raw SCSI command bytes (CDB).
     * @param expectedResponseLength The expected length of the response.
     * @param endpointIn The input endpoint address.
     * @param endpointOut The output endpoint address.
     * @param timeout The timeout in milliseconds.
     * @return The response bytes from the device.
     */
    external fun executeScsiCommand(
        fd: Int,
        command: ByteArray,
        expectedResponseLength: Int,
        endpointIn: Int = 0x81,
        endpointOut: Int = 0x01,
        timeout: Int = 5000
    ): ByteArray?

    companion object {
        init {
            System.loadLibrary("bitperfect-driver")
        }
    }
}

#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include <unistd.h>
#include <cstring>
#include <linux/usbdevice_fs.h>
#include <sys/ioctl.h>
#include <errno.h>

#define LOG_TAG "BitPerfectDriver"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// SCSI commands
#define SCSI_INQUIRY 0x12
#define SCSI_MODE_SENSE_10 0x5A
#define SCSI_MODE_SENSE_6 0x1A
#define SCSI_READ_TOC 0x43
#define SCSI_READ_CD 0xBE

extern "C" JNIEXPORT jstring JNICALL
Java_com_bitperfect_driver_ScsiDriver_getDriverVersion(
        JNIEnv* env,
        jobject /* this */) {
    std::string version = "0.1.0-alpha";
    return env->NewStringUTF(version.c_str());
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_bitperfect_driver_ScsiDriver_executeScsiCommand(
        JNIEnv* env,
        jobject /* this */,
        jint fd,
        jbyteArray command,
        jint expectedResponseLength,
        jint endpointIn,
        jint endpointOut,
        jint timeout) {

    jsize cmdLen = env->GetArrayLength(command);
    std::vector<uint8_t> cmdBuffer(cmdLen);
    env->GetByteArrayRegion(command, 0, cmdLen, reinterpret_cast<jbyte*>(cmdBuffer.data()));

    uint8_t opcode = cmdBuffer[0];
    LOGI("Executing SCSI command: 0x%02X, fd: %d, epIn: %d, epOut: %d", opcode, fd, endpointIn, endpointOut);

    // If fd is -1, we use mock responses for testing
    if (fd == -1) {
        std::vector<uint8_t> response;
        switch (opcode) {
        case SCSI_INQUIRY: {
            LOGI("Handling INQUIRY");
            // Standard Inquiry Data (36 bytes)
            response = {
                0x05, // Peripheral Device Type: CD-ROM
                0x80, // Removable
                0x05, // Version: SPC-3
                0x02, // Response Data Format
                0x1F, // Additional Length
                0x00, 0x00, 0x00,
                'B', 'i', 't', 'P', 'e', 'r', 'f', 'e', // Vendor (8 bytes)
                'c', 't', ' ', 'V', 'i', 'r', 't', 'u', // Product (16 bytes)
                'a', 'l', ' ', 'D', 'r', 'i', 'v', 'e',
                '1', '.', '0', '0'                      // Revision (4 bytes)
            };
            break;
        }
        case SCSI_MODE_SENSE_10:
        case SCSI_MODE_SENSE_6: {
            uint8_t pageCode = cmdBuffer[2] & 0x3F;
            LOGI("Handling MODE SENSE, Page Code: 0x%02X", pageCode);

            if (pageCode == 0x2A) { // CD-ROM Capabilities and Mechanical Status Page
                response.resize(expectedResponseLength, 0);
                // Header (simplified)
                if (opcode == SCSI_MODE_SENSE_10) {
                    response[0] = 0; response[1] = 0x1E; // Length
                } else {
                    response[0] = 0x1E; // Length
                }

                // Page 2A data
                // Offset depends on header length (8 for MS10, 4 for MS6)
                int offset = (opcode == SCSI_MODE_SENSE_10) ? 8 : 4;
                if (response.size() > offset + 2) {
                    response[offset] = 0x2A; // Page Code
                    response[offset+1] = 0x12; // Page Length
                    response[offset+2] = 0x01; // C2 Error pointers supported
                    response[offset+3] = 0x00;
                }
            } else {
                // Default mock response for other pages
                response.resize(expectedResponseLength, 0);
            }
            break;
        }
        case SCSI_READ_TOC: {
            LOGI("Handling READ TOC");
            // Mock TOC with 2 tracks
            response = {
                0x00, 0x12, // TOC Data Length (18 bytes)
                0x01,       // First Track
                0x02,       // Last Track
                // Track 1
                0x00, 0x14, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00,
                // Track 2 (Lead-out)
                0x00, 0x14, 0xAA, 0x00, 0x00, 0x00, 0x32, 0x00
            };
            break;
        }
            case SCSI_READ_CD: {
                LOGI("Handling READ CD (Mock)");
                response.resize(expectedResponseLength, 0xAA);
                break;
            }
            default:
                LOGE("Unsupported mock SCSI command: 0x%02X", opcode);
                return nullptr;
        }

        jbyteArray result = env->NewByteArray(response.size());
        env->SetByteArrayRegion(result, 0, response.size(), reinterpret_cast<const jbyte*>(response.data()));
        return result;
    }

    // Real USB hardware communication using Bulk transfers
    // 1. Send Command (CBW - Command Block Wrapper)
    struct usbdevfs_bulktransfer bulk;
    std::vector<uint8_t> cbw(31, 0);
    cbw[0] = 0x55; cbw[1] = 0x53; cbw[2] = 0x42; cbw[3] = 0x43; // dCBWSignature
    cbw[4] = 0x01; // dCBWTag (can be anything)

    // dCBWDataTransferLength
    cbw[8] = expectedResponseLength & 0xFF;
    cbw[9] = (expectedResponseLength >> 8) & 0xFF;
    cbw[10] = (expectedResponseLength >> 16) & 0xFF;
    cbw[11] = (expectedResponseLength >> 24) & 0xFF;

    cbw[12] = 0x80; // bmCBWFlags (Bit 7: Direction, 1 = In)
    cbw[13] = 0;    // bCBWLUN
    cbw[14] = cmdLen; // bCBWCBLength

    // Copy SCSI command
    std::memcpy(&cbw[15], cmdBuffer.data(), cmdLen);

    bulk.ep = endpointOut;
    bulk.len = cbw.size();
    bulk.data = cbw.data();
    bulk.timeout = timeout;

    int res = ioctl(fd, USBDEVFS_BULK, &bulk);
    if (res < 0) {
        LOGE("USB Out Bulk Transfer failed: %s (errno: %d)", strerror(errno), errno);
        return nullptr;
    }

    // 2. Receive Data
    std::vector<uint8_t> response(expectedResponseLength);
    bulk.ep = endpointIn;
    bulk.len = expectedResponseLength;
    bulk.data = response.data();
    bulk.timeout = timeout;

    res = ioctl(fd, USBDEVFS_BULK, &bulk);
    if (res < 0) {
        LOGE("USB In Bulk Transfer failed: %s (errno: %d)", strerror(errno), errno);
        return nullptr;
    }

    // 3. Receive Status (CSW - Command Status Wrapper)
    std::vector<uint8_t> csw(13);
    bulk.ep = endpointIn;
    bulk.len = csw.size();
    bulk.data = csw.data();
    bulk.timeout = timeout;

    res = ioctl(fd, USBDEVFS_BULK, &bulk);
    if (res < 0) {
        LOGE("USB CSW Bulk Transfer failed: %s (errno: %d)", strerror(errno), errno);
    }

    jbyteArray result = env->NewByteArray(response.size());
    env->SetByteArrayRegion(result, 0, response.size(), reinterpret_cast<const jbyte*>(response.data()));
    return result;
}

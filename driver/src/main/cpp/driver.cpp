#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include <unistd.h>
#include <cstring>

#define LOG_TAG "BitPerfectDriver"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// SCSI commands
#define SCSI_INQUIRY 0x12
#define SCSI_MODE_SENSE_10 0x5A
#define SCSI_MODE_SENSE_6 0x1A

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
        jint expectedResponseLength) {

    jsize cmdLen = env->GetArrayLength(command);
    std::vector<uint8_t> cmdBuffer(cmdLen);
    env->GetByteArrayRegion(command, 0, cmdLen, reinterpret_cast<jbyte*>(cmdBuffer.data()));

    uint8_t opcode = cmdBuffer[0];
    LOGI("Executing SCSI command: 0x%02X, fd: %d", opcode, fd);

    // In this phase, we are mocking the hardware responses.
    // In Phase 2, this will be replaced with real USB bulk transfers or SG_IO.

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
        default:
            LOGE("Unsupported SCSI command: 0x%02X", opcode);
            return nullptr;
    }

    if (response.empty()) return nullptr;

    jbyteArray result = env->NewByteArray(response.size());
    env->SetByteArrayRegion(result, 0, response.size(), reinterpret_cast<const jbyte*>(response.data()));
    return result;
}

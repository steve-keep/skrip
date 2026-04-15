# Phase 1: Communication & Diagnostics - Implementation Plan

## Overview
Phase 1 focuses on establishing a robust communication layer between the Android application and external USB optical drives. This involves handling USB permissions, hardware discovery, and executing low-level SCSI commands for drive diagnostics.

## 1. Project Infrastructure
* **Multi-Module Setup:**
    * `:app`: Jetpack Compose UI, Permission handling, and ViewModels.
    * `:core`: Business logic, AccurateRip/CRC (stubs for now), and Repository layers.
    * `:driver`: NDK-based module for raw SCSI/MMC communication.
* **Build System:** Configure Gradle with Version Catalogs for dependency management.
* **CI/CD Base:** Initial GitHub Actions workflow for building the APK.

## 2. Driver Layer (NDK/C++)
* **JNI Interface:** Define the bridge between Kotlin and C++ for passing `UsbDeviceConnection` file descriptors.
* **SCSI Implementation:**
    * Implement raw SCSI command wrapper using `ioctl` or direct bulk transfers via Android's `UsbDeviceConnection`.
    * Focus on `INQUIRY` (0x12) to get Vendor/Product info.
    * Focus on `MODE SENSE` (0x5A/0x1A) to detect capabilities (C2, Cache).
* **Logging:** Implement a native logging bridge that pipes C++ logs back to the UI.

## 3. Hardware Discovery & Permissions
* **USB Filtering:** Implement a `UsbManager` wrapper that filters for `USB_CLASS_MASS_STORAGE` and specifically looks for CD-ROM sub-classes.
* **Permission Flow:** Reactive UI flow to request and handle USB attachment/permission events.

## 4. User Interface (Jetpack Compose)
* **Device Selection Screen:** List compatible drives.
* **Diagnostic Dashboard:**
    * Display Inquiry data (Manufacturer, Model, Firmware Revision).
    * Display detected capabilities (C2 Support, Cache size).
* **Live Terminal View:** A scrollable text view that shows real-time SCSI command/response logs for debugging and user feedback.

## 5. Mocking & Verification
* **Hardware Mocking:** Create a "Demo Mode" or Mock Driver that simulates a CD-ROM drive response for testing in environments without physical hardware.
* **Logging System:** Ensure all raw byte transfers are logged in hex format for forensic-level debugging.

## Task List
- [ ] Initialize Android Project with `:app`, `:core`, `:driver` modules.
- [ ] Configure C++ support (CMake) in `:driver`.
- [ ] Implement `UsbManager` discovery logic in `:app`.
- [ ] Implement JNI bridge for `UsbDeviceConnection`.
- [ ] Implement C++ SCSI command execution (Inquiry/Mode Sense).
- [ ] Build Compose UI for Device Selection and Diagnostics.
- [ ] Implement Live Log observer for UI.
- [ ] Add unit tests for command parsing.

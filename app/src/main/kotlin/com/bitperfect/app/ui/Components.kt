package com.bitperfect.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.hardware.usb.UsbDevice

@Composable
fun DeviceList(
    devices: List<UsbDevice>,
    onDeviceClick: (UsbDevice) -> Unit
) {
    Column {
        Text(
            text = "Select USB Drive",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn {
            items(devices) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = { onDeviceClick(device) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Vendor ID: ${device.vendorId}")
                        Text(text = "Product ID: ${device.productId}")
                        Text(text = "Name: ${device.deviceName}")
                    }
                }
            }
        }
    }
}

@Composable
fun DiagnosticDashboard(
    inquiryData: String,
    capabilities: List<String>,
    logs: List<String>
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Drive Info", style = MaterialTheme.typography.titleMedium)
                Text(text = inquiryData)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Capabilities", style = MaterialTheme.typography.titleMedium)
                capabilities.forEach { capability ->
                    Text(text = "• $capability")
                }
            }
        }

        Text(
            text = "Live Log",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = MaterialTheme.shapes.medium
        ) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(logs) { log ->
                    Text(
                        text = log,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            }
        }
    }
}

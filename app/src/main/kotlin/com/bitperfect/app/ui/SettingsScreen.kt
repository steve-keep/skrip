package com.bitperfect.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bitperfect.app.ui.theme.*
import com.bitperfect.core.utils.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    onCopyDebugReport: () -> Unit
) {
    var isVirtualDriveEnabled by remember { mutableStateOf(settingsManager.isVirtualDriveEnabled) }
    var selectedTestCdIndex by remember { mutableStateOf(settingsManager.selectedTestCdIndex) }
    var outputFolderUri by remember { mutableStateOf(settingsManager.outputFolderUri) }
    var namingScheme by remember { mutableStateOf(settingsManager.namingScheme) }
    var isAccurateRipEnabled by remember { mutableStateOf(settingsManager.isAccurateRipEnabled) }
    var isC2ErrorPointersEnabled by remember { mutableStateOf(settingsManager.isC2ErrorPointersEnabled) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(it, takeFlags)

            val uriString = it.toString()
            outputFolderUri = uriString
            settingsManager.outputFolderUri = uriString
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = Typography.headingMd.copy(color = Color.White)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = com.bitperfect.app.R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            tint = AccentPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "BitPerfect",
                            style = Typography.headingSm.copy(color = AccentPrimary)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgBase,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = BgBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            item {
                SettingsSectionHeader(
                    title = "Storage & Paths",
                    description = "Configure where and how your rips are saved."
                )
                SettingsCard {
                    DestinationFolderInput(
                        value = outputFolderUri,
                        onClick = { folderPickerLauncher.launch(null) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    NamingSchemeInput(
                        value = namingScheme,
                        onValueChange = {
                            namingScheme = it
                            settingsManager.namingScheme = it
                        }
                    )
                }
            }

            item {
                SettingsSectionHeader(
                    title = "Verification",
                    description = "Ensure bit-perfect accuracy during extraction."
                )
                SettingsCard {
                    SettingsRow(
                        title = "AccurateRip",
                        description = "Verify rips against the online AccurateRip database to ensure zero errors.",
                        trailingContent = {
                            DesignSwitch(
                                checked = isAccurateRipEnabled,
                                onCheckedChange = {
                                    isAccurateRipEnabled = it
                                    settingsManager.isAccurateRipEnabled = it
                                },
                                isAccurateRip = true
                            )
                        }
                    )
                    SettingsRow(
                        title = "C2 Error Pointers",
                        description = "Utilize drive hardware to detect errors. Disable if your drive does not support C2 reliably.",
                        showDivider = false,
                        trailingContent = {
                            DesignSwitch(
                                checked = isC2ErrorPointersEnabled,
                                onCheckedChange = {
                                    isC2ErrorPointersEnabled = it
                                    settingsManager.isC2ErrorPointersEnabled = it
                                }
                            )
                        }
                    )
                }
            }

            // Hidden behind "Development & Testing" just for backward compat. tests if needed
            // But we can keep it exactly as it was, just appended
            item {
                Text(
                    text = "Development & Testing",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            item {
                PreferenceSwitch(
                    title = "Enable Virtual Drive",
                    description = "Simulate a CD drive for UI testing",
                    checked = isVirtualDriveEnabled,
                    onCheckedChange = {
                        isVirtualDriveEnabled = it
                        settingsManager.isVirtualDriveEnabled = it
                    }
                )
            }

            if (isVirtualDriveEnabled) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Selected Test CD",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }
                }

                itemsIndexed(settingsManager.testCds) { index, cd ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTestCdIndex = index
                                settingsManager.selectedTestCdIndex = index
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 24.dp, vertical = 12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedTestCdIndex == index,
                                onClick = {
                                    selectedTestCdIndex = index
                                    settingsManager.selectedTestCdIndex = index
                                }
                            )
                            Column(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = cd.album,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cd.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Support & Debug",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            item {
                PreferenceItem(
                    title = "Copy Debug Report",
                    description = "Copy system info, logs, and crash reports to clipboard",
                    onClick = onCopyDebugReport
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                PreferencesHintCard(
                    title = "About Virtual Drive",
                    description = "The virtual drive uses deterministic mock data to simulate successful rips and error scenarios without physical hardware."
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

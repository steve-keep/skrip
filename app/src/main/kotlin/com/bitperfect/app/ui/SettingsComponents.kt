package com.bitperfect.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitperfect.app.ui.theme.*

@Composable
fun SettingsSectionHeader(title: String, description: String) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)) {
        Text(
            text = title,
            style = Typography.headingMd,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = Typography.bodyMd,
            color = Color(0x99FFFFFF) // --text-secondary
        )
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(BgSurface, RoundedCornerShape(14.dp))
            .border(1.dp, BorderDefault, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun ValidSyntaxBadge() {
    Box(
        modifier = Modifier
            .background(AccentPrimarySubtle, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = "Valid syntax",
            style = Typography.labelCaps,
            color = AccentPrimary
        )
    }
}

@Composable
fun DesignSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    isAccurateRip: Boolean = false
) {
    val trackColor = if (checked) AccentPrimary else Color(0x26FFFFFF)

    Box(
        modifier = Modifier
            .size(width = 51.dp, height = 31.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(onClick = { onCheckedChange?.invoke(!checked) })
            .padding(2.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(27.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (checked && isAccurateRip) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(ColorInfoBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    description: String,
    trailingContent: @Composable () -> Unit,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            Text(
                text = title,
                style = Typography.headingSm,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = Typography.bodyMd,
                color = Color(0x99FFFFFF)
            )
        }
        trailingContent()
    }
    if (showDivider) {
        HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(horizontal = 0.dp))
    }
}

@Composable
fun NamingSchemeInput(value: String, onValueChange: (String) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Naming Scheme",
                style = Typography.bodyMd,
                color = Color(0x99FFFFFF)
            )
            ValidSyntaxBadge()
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSurfaceRaised, RoundedCornerShape(10.dp))
                .border(1.dp, BorderInput, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = Typography.monoMd.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Output: Pink Floyd - 1973 - The Dark Side of the Moon/01 - Speak to Me.flac",
            style = Typography.monoSm,
            color = Color(0x8C3DDC68) // --text-mono-dim roughly
        )
    }
}

@Composable
fun DestinationFolderInput(value: String?, onClick: () -> Unit) {
    Column {
        Text(
            text = "Destination Folder",
            style = Typography.bodyMd,
            color = Color(0x99FFFFFF)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(BgSurfaceRaised, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderInput, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Folder",
                        tint = Color(0x99FFFFFF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = value ?: "/Volumes/AudioArchive",
                        style = Typography.monoMd,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(BgSurfaceRaised, RoundedCornerShape(10.dp))
                    .border(1.dp, BorderInput, RoundedCornerShape(10.dp))
                    .clickable(onClick = onClick)
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Browse",
                    style = Typography.bodyMd,
                    color = Color.White
                )
            }
        }
    }
}

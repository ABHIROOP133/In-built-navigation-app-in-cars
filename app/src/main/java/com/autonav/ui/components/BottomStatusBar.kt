package com.autonav.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.autonav.data.model.ApplicationStatus
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.NavigationStatus
import com.autonav.data.model.SystemStatusSnapshot
import com.autonav.ui.theme.CharcoalSurface
import com.autonav.ui.theme.StatusDisconnectedTone
import com.autonav.ui.theme.StatusInactiveTone
import com.autonav.ui.theme.StatusReadyTone
import com.autonav.ui.theme.SteelBlueAccent
import com.autonav.ui.theme.StructuralBorder
import com.autonav.ui.theme.TextPrimary
import com.autonav.ui.theme.TextSecondary

/**
 * Bottom system status strip reporting truthful hardware and navigation subsystem states:
 * - GPS status: Not connected / Searching / Connected (X sats)
 * - Vehicle speed: XX km/h or "-- km/h"
 * - Navigation status: Inactive
 * - Application status: Ready
 */
@Composable
fun BottomStatusBar(
    systemStatus: SystemStatusSnapshot,
    isCompactLayout: Boolean = false,
    modifier: Modifier = Modifier
) {
    val containerModifier = modifier
        .fillMaxWidth()
        .background(CharcoalSurface, RoundedCornerShape(4.dp))
        .border(width = 1.dp, color = StructuralBorder, shape = RoundedCornerShape(4.dp))
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .testTag("bottom_status_bar")

    val gpsDisplayValue = when (systemStatus.gpsStatus) {
        GpsConnectionStatus.CONNECTED -> {
            if (systemStatus.satelliteCount > 0) "${systemStatus.gpsStatus.displayLabel} (${systemStatus.satelliteCount} sats)"
            else systemStatus.gpsStatus.displayLabel
        }
        else -> systemStatus.gpsStatus.displayLabel
    }

    if (isCompactLayout) {
        Column(
            modifier = containerModifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusIndicatorItem(
                label = "GPS status",
                value = gpsDisplayValue,
                indicatorColor = gpsIndicatorColor(systemStatus.gpsStatus),
                testTag = "status_item_gps"
            )
            StatusIndicatorItem(
                label = "Vehicle speed",
                value = systemStatus.formattedSpeed,
                indicatorColor = if (systemStatus.gpsStatus == GpsConnectionStatus.CONNECTED) SteelBlueAccent else StatusInactiveTone,
                testTag = "status_item_speed"
            )
            StatusIndicatorItem(
                label = "Navigation status",
                value = systemStatus.navigationStatus.displayLabel,
                indicatorColor = navigationIndicatorColor(systemStatus.navigationStatus),
                testTag = "status_item_navigation"
            )
            StatusIndicatorItem(
                label = "Application status",
                value = systemStatus.applicationStatus.displayLabel,
                indicatorColor = applicationIndicatorColor(systemStatus.applicationStatus),
                testTag = "status_item_application"
            )
        }
    } else {
        Row(
            modifier = containerModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatusIndicatorItem(
                label = "GPS status",
                value = gpsDisplayValue,
                indicatorColor = gpsIndicatorColor(systemStatus.gpsStatus),
                testTag = "status_item_gps"
            )
            StatusIndicatorItem(
                label = "Vehicle speed",
                value = systemStatus.formattedSpeed,
                indicatorColor = if (systemStatus.gpsStatus == GpsConnectionStatus.CONNECTED) SteelBlueAccent else StatusInactiveTone,
                testTag = "status_item_speed"
            )
            StatusIndicatorItem(
                label = "Navigation status",
                value = systemStatus.navigationStatus.displayLabel,
                indicatorColor = navigationIndicatorColor(systemStatus.navigationStatus),
                testTag = "status_item_navigation"
            )
            StatusIndicatorItem(
                label = "Application status",
                value = systemStatus.applicationStatus.displayLabel,
                indicatorColor = applicationIndicatorColor(systemStatus.applicationStatus),
                testTag = "status_item_application"
            )
        }
    }
}

@Composable
private fun StatusIndicatorItem(
    label: String,
    value: String,
    indicatorColor: Color,
    testTag: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = indicatorColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary
        )
    }
}

private fun gpsIndicatorColor(status: GpsConnectionStatus): Color = when (status) {
    GpsConnectionStatus.NOT_CONNECTED -> StatusDisconnectedTone
    GpsConnectionStatus.SEARCHING -> StatusDisconnectedTone
    GpsConnectionStatus.CONNECTED -> StatusReadyTone
}

private fun navigationIndicatorColor(status: NavigationStatus): Color = when (status) {
    NavigationStatus.INACTIVE -> StatusInactiveTone
    NavigationStatus.CALCULATING_ROUTE -> StatusDisconnectedTone
    NavigationStatus.ACTIVE -> StatusReadyTone
    NavigationStatus.REROUTING -> StatusDisconnectedTone
}

private fun applicationIndicatorColor(status: ApplicationStatus): Color = when (status) {
    ApplicationStatus.INITIALIZING -> StatusDisconnectedTone
    ApplicationStatus.READY -> StatusReadyTone
    ApplicationStatus.DEGRADED -> StatusDisconnectedTone
}

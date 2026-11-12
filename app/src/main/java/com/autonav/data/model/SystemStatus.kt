package com.autonav.data.model

import kotlin.math.roundToInt

/**
 * Represents the hardware GPS receiver connection state.
 */
enum class GpsConnectionStatus(val displayLabel: String) {
    NOT_CONNECTED("Not connected"),
    SEARCHING("Searching"),
    CONNECTED("Connected")
}

/**
 * Represents the turn-by-turn navigation engine state.
 */
enum class NavigationStatus(val displayLabel: String) {
    INACTIVE("Inactive"),
    CALCULATING_ROUTE("Calculating route"),
    ACTIVE("Active"),
    REROUTING("Rerouting")
}

/**
 * Represents the overall infotainment application lifecycle state.
 */
enum class ApplicationStatus(val displayLabel: String) {
    INITIALIZING("Initializing"),
    READY("Ready"),
    DEGRADED("Degraded")
}

/**
 * Immutable snapshot of system-wide status indicators displayed in the bottom telemetry bar.
 *
 * Truthful telemetry design:
 * - [currentSpeedKmh] is null when stationary, unavailable, or GPS is disconnected.
 * - Formatted speed displays "-- km/h" when not connected or unavailable, never fake numbers.
 */
data class SystemStatusSnapshot(
    val gpsStatus: GpsConnectionStatus = GpsConnectionStatus.NOT_CONNECTED,
    val navigationStatus: NavigationStatus = NavigationStatus.INACTIVE,
    val applicationStatus: ApplicationStatus = ApplicationStatus.READY,
    val currentSpeedKmh: Float? = null,
    val lastKnownLocation: LocationFix? = null,
    val satelliteCount: Int = 0
) {
    val formattedGpsStatus: String
        get() = when (gpsStatus) {
            GpsConnectionStatus.CONNECTED -> {
                if (satelliteCount > 0) "GPS status: Connected ($satelliteCount sats)"
                else "GPS status: Connected"
            }
            else -> "GPS status: ${gpsStatus.displayLabel}"
        }

    val formattedNavigationStatus: String
        get() = "Navigation status: ${navigationStatus.displayLabel}"

    val formattedApplicationStatus: String
        get() = "Application status: ${applicationStatus.displayLabel}"

    val formattedSpeed: String
        get() = if (gpsStatus == GpsConnectionStatus.CONNECTED && currentSpeedKmh != null) {
            "${currentSpeedKmh.roundToInt()} km/h"
        } else {
            "-- km/h"
        }
}

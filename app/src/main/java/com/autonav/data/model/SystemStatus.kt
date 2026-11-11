package com.autonav.data.model

/**
 * Represents the hardware GPS receiver connection state.
 * Day 1 Foundation strictly reports [NOT_CONNECTED] as GPS tracking is scheduled for Milestone 2.
 */
enum class GpsConnectionStatus(val displayLabel: String) {
    NOT_CONNECTED("Not connected"),
    SEARCHING("Searching"),
    CONNECTED("Connected")
}

/**
 * Represents the turn-by-turn navigation engine state.
 * Day 1 Foundation strictly reports [INACTIVE] as route guidance is scheduled for Milestone 4.
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
 * Intentionally excludes GPS coordinates, speed telemetry, or route metrics on Day 1
 * so that unimplemented hardware/navigation features are never simulated or faked.
 */
data class SystemStatusSnapshot(
    val gpsStatus: GpsConnectionStatus = GpsConnectionStatus.NOT_CONNECTED,
    val navigationStatus: NavigationStatus = NavigationStatus.INACTIVE,
    val applicationStatus: ApplicationStatus = ApplicationStatus.READY
) {
    val formattedGpsStatus: String
        get() = "GPS status: ${gpsStatus.displayLabel}"

    val formattedNavigationStatus: String
        get() = "Navigation status: ${navigationStatus.displayLabel}"

    val formattedApplicationStatus: String
        get() = "Application status: ${applicationStatus.displayLabel}"
}

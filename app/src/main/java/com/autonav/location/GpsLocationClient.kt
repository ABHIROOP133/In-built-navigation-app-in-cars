package com.autonav.location

import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.LocationFix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Hardware abstraction contract for vehicle GPS telemetry.
 */
interface GpsLocationClient {
    /**
     * Continuous stream of geographic location, speed, and heading fixes.
     */
    val locationUpdates: Flow<LocationFix>

    /**
     * Real-time GPS connection status (NOT_CONNECTED, SEARCHING, CONNECTED).
     */
    val connectionStatus: StateFlow<GpsConnectionStatus>

    /**
     * Real-time count of active GNSS satellites used in navigation fixes.
     */
    val satelliteCount: StateFlow<Int>

    /**
     * Begins listening for GNSS sensor updates.
     */
    fun startTracking()

    /**
     * Stops listening for GNSS sensor updates to conserve battery / compute resources.
     */
    fun stopTracking()
}


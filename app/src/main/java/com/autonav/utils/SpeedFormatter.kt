package com.autonav.utils

import kotlin.math.roundToInt

/**
 * Automotive telemetry formatters for vehicle speed and compass bearings.
 */
object SpeedFormatter {

    private const val MPS_TO_KMH_FACTOR = 3.6f

    /**
     * Converts meters per second (Android Location standard) to kilometers per hour.
     */
    fun metersPerSecondToKmh(speedMps: Float): Float {
        return if (speedMps > 0f) speedMps * MPS_TO_KMH_FACTOR else 0f
    }

    /**
     * Formats vehicle speed into a glanceable automotive HUD string.
     * Returns "-- km/h" when speed is null or negative.
     */
    fun formatSpeed(speedKmh: Float?): String {
        return if (speedKmh != null && speedKmh >= 0f) {
            "${speedKmh.roundToInt()} km/h"
        } else {
            "-- km/h"
        }
    }

    /**
     * Converts a 0.0 - 360.0 degree heading into a cardinal compass direction (N, NE, E, SE, S, SW, W, NW).
     */
    fun bearingToCardinal(bearingDegrees: Float?): String {
        if (bearingDegrees == null) return "--"
        val normalized = ((bearingDegrees % 360f) + 360f) % 360f
        return when {
            normalized >= 337.5f || normalized < 22.5f -> "N"
            normalized < 67.5f -> "NE"
            normalized < 112.5f -> "E"
            normalized < 157.5f -> "SE"
            normalized < 202.5f -> "S"
            normalized < 247.5f -> "SW"
            normalized < 292.5f -> "W"
            else -> "NW"
        }
    }
}


package com.autonav.data.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Immutable domain model representing a geographic location fix from vehicle GPS sensors.
 *
 * @property latitude WGS84 latitude coordinate in degrees (-90.0 to 90.0).
 * @property longitude WGS84 longitude coordinate in degrees (-180.0 to 180.0).
 * @property bearing Compass heading / trajectory azimuth in degrees (0.0 to 360.0), or null if stationary.
 * @property speedKmh Vehicle ground speed in kilometers per hour, or null if stationary or unavailable.
 * @property accuracyMeters Estimated horizontal accuracy radius in meters (68% confidence level).
 * @property altitudeMeters Altitude above WGS84 ellipsoid in meters.
 * @property timestampMillis Epoch timestamp when this fix was acquired.
 */
data class LocationFix(
    val latitude: Double,
    val longitude: Double,
    val bearing: Float? = null,
    val speedKmh: Float? = null,
    val accuracyMeters: Float? = null,
    val altitudeMeters: Double? = null,
    val timestampMillis: Long = System.currentTimeMillis()
) {
    val isValid: Boolean
        get() = latitude in -90.0..90.0 && longitude in -180.0..180.0

    /**
     * Calculates the great-circle distance to another coordinate fix in meters using the Haversine formula.
     */
    fun distanceTo(other: LocationFix): Float {
        val earthRadiusMeters = 6371000.0
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (earthRadiusMeters * c).toFloat()
    }

    /**
     * Calculates the forward azimuth / bearing in degrees (0.0 to 360.0) from this location to [other].
     */
    fun bearingTo(other: LocationFix): Float {
        val lat1 = Math.toRadians(latitude)
        val lat2 = Math.toRadians(other.latitude)
        val dLon = Math.toRadians(other.longitude - longitude)

        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        val initialBearing = Math.toDegrees(atan2(y, x))
        return ((initialBearing + 360.0) % 360.0).toFloat()
    }
}


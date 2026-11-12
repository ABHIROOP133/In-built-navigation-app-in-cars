package com.autonav.data.model

import kotlin.math.roundToInt

/**
 * Categorization of automotive points of interest (POIs) and search destinations.
 */
enum class DestinationCategory(val displayLabel: String) {
    GENERAL("Destination"),
    CHARGING_STATION("EV Charging"),
    FUEL("Fuel Station"),
    AIRPORT("Airport"),
    TRANSIT("Transit Station"),
    HOSPITAL("Hospital / Medical"),
    PARKING("Parking")
}

/**
 * Immutable domain entity representing a search destination or waypoint.
 *
 * @property id Unique destination identifier or place ID.
 * @property title Primary name of the place (e.g. "Kempegowda International Airport").
 * @property subtitle Address line or geographic neighborhood.
 * @property latitude WGS84 latitude.
 * @property longitude WGS84 longitude.
 * @property category Destination category for automotive filtering.
 * @property distanceMeters Estimated straight-line distance from current vehicle location.
 */
data class Destination(
    val id: String,
    val title: String,
    val subtitle: String,
    val latitude: Double,
    val longitude: Double,
    val category: DestinationCategory = DestinationCategory.GENERAL,
    val distanceMeters: Float? = null
) {
    val formattedDistance: String
        get() = when {
            distanceMeters == null -> ""
            distanceMeters < 1000f -> "${distanceMeters.roundToInt()} m"
            else -> {
                val km = distanceMeters / 1000f
                val rounded = (km * 10).roundToInt() / 10.0
                "$rounded km"
            }
        }
}


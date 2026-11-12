package com.autonav.data.repository

import com.autonav.data.model.Destination
import com.autonav.data.model.DestinationCategory
import com.autonav.data.model.LocationFix
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Contract for searching and resolving geographic destinations and automotive POIs.
 */
interface DestinationRepository {
    /**
     * Resolves matching destinations ranked by text relevance and proximity to [currentFix].
     */
    suspend fun searchDestinations(query: String, currentFix: LocationFix?): List<Destination>

    /**
     * Returns suggested automotive destinations (EV charging, fuel, major transit hubs).
     */
    suspend fun getSuggestedDestinations(currentFix: LocationFix?): List<Destination>
}

/**
 * Automotive destination repository implementation with offline automotive POI catalog
 * and geographic proximity ranking.
 */
class DefaultDestinationRepository(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : DestinationRepository {

    private val baseCatalog = listOf(
        Destination(
            id = "poi_blr_airport",
            title = "Kempegowda International Airport (BLR)",
            subtitle = "KIAL Rd, Devanahalli, Bengaluru, Karnataka",
            latitude = 13.1986,
            longitude = 77.7066,
            category = DestinationCategory.AIRPORT
        ),
        Destination(
            id = "poi_blr_majestic",
            title = "KSR Bengaluru City Railway Station (Majestic)",
            subtitle = "Kempegowda, Sevashrama, Bengaluru, Karnataka",
            latitude = 12.9781,
            longitude = 77.5695,
            category = DestinationCategory.TRANSIT
        ),
        Destination(
            id = "poi_blr_cantonment",
            title = "Bengaluru Cantonment Railway Station",
            subtitle = "Cantonment Railway Station Rd, Vasanth Nagar",
            latitude = 12.9937,
            longitude = 77.5982,
            category = DestinationCategory.TRANSIT
        ),
        Destination(
            id = "poi_blr_ecity",
            title = "Electronic City Phase 1 Hub",
            subtitle = "Hosur Rd, Electronic City, Bengaluru",
            latitude = 12.8452,
            longitude = 77.6602,
            category = DestinationCategory.GENERAL
        ),
        Destination(
            id = "poi_blr_whitefield",
            title = "International Tech Park Bangalore (ITPB)",
            subtitle = "Whitefield Main Rd, Pattandur Agrahara",
            latitude = 12.9863,
            longitude = 77.7383,
            category = DestinationCategory.GENERAL
        ),
        Destination(
            id = "poi_blr_indiranagar",
            title = "Indiranagar 100 Feet Road",
            subtitle = "100 Feet Rd, HAL 2nd Stage, Indiranagar",
            latitude = 12.9719,
            longitude = 77.6412,
            category = DestinationCategory.GENERAL
        ),
        Destination(
            id = "poi_blr_ev_fast",
            title = "Tata Power Superfast EV Charging Station",
            subtitle = "Outer Ring Rd, Bellandur, Bengaluru",
            latitude = 12.9298,
            longitude = 77.6833,
            category = DestinationCategory.CHARGING_STATION
        ),
        Destination(
            id = "poi_blr_fuel_shell",
            title = "Shell Ultra Fuel Station & Service Hub",
            subtitle = "Old Airport Rd, Kodihalli, Bengaluru",
            latitude = 12.9601,
            longitude = 77.6492,
            category = DestinationCategory.FUEL
        ),
        Destination(
            id = "poi_blr_manipal",
            title = "Manipal Hospital Emergency Medical Centre",
            subtitle = "98 HAL Old Airport Rd, Kodihalli",
            latitude = 12.9582,
            longitude = 77.6481,
            category = DestinationCategory.HOSPITAL
        )
    )

    override suspend fun searchDestinations(query: String, currentFix: LocationFix?): List<Destination> =
        withContext(dispatcher) {
            val trimmed = query.trim()
            if (trimmed.isEmpty()) {
                return@withContext getSuggestedDestinations(currentFix)
            }

            val tokens = trimmed.lowercase().split("\\s+".toRegex())

            baseCatalog
                .filter { dest ->
                    val combinedText = "${dest.title} ${dest.subtitle} ${dest.category.displayLabel}".lowercase()
                    tokens.all { token -> combinedText.contains(token) }
                }
                .map { dest ->
                    attachDistance(dest, currentFix)
                }
                .sortedWith(
                    compareBy<Destination> { dest ->
                        // Exact prefix matches rank first
                        val titleLower = dest.title.lowercase()
                        if (titleLower.startsWith(trimmed.lowercase())) 0 else 1
                    }.thenBy { dest ->
                        dest.distanceMeters ?: Float.MAX_VALUE
                    }
                )
        }

    override suspend fun getSuggestedDestinations(currentFix: LocationFix?): List<Destination> =
        withContext(dispatcher) {
            baseCatalog
                .map { dest -> attachDistance(dest, currentFix) }
                .sortedBy { dest -> dest.distanceMeters ?: Float.MAX_VALUE }
        }

    private fun attachDistance(dest: Destination, currentFix: LocationFix?): Destination {
        if (currentFix == null || !currentFix.isValid) return dest
        val target = LocationFix(latitude = dest.latitude, longitude = dest.longitude)
        val distance = currentFix.distanceTo(target)
        return dest.copy(distanceMeters = distance)
    }
}

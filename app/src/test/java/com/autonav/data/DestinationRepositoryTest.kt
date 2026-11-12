package com.autonav.data

import com.autonav.data.model.DestinationCategory
import com.autonav.data.model.LocationFix
import com.autonav.data.repository.DefaultDestinationRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DestinationRepositoryTest {

    private val repository = DefaultDestinationRepository()
    private val sampleVehicleFix = LocationFix(
        latitude = 12.9716,
        longitude = 77.5946,
        speedKmh = 40f,
        bearing = 90f
    )

    @Test
    fun `searchDestinations with airport query returns airport POI with distance`() = runBlocking {
        val results = repository.searchDestinations("Kempegowda Airport", sampleVehicleFix)

        assertTrue(results.isNotEmpty())
        val first = results.first()
        assertTrue(first.title.contains("Airport"))
        assertEquals(DestinationCategory.AIRPORT, first.category)
        assertNotNull(first.distanceMeters)
        assertTrue(first.distanceMeters!! > 0f)
    }

    @Test
    fun `searchDestinations with EV query returns charging stations`() = runBlocking {
        val results = repository.searchDestinations("charging", sampleVehicleFix)

        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.category == DestinationCategory.CHARGING_STATION })
    }

    @Test
    fun `searchDestinations with blank query returns suggested destinations sorted by proximity`() = runBlocking {
        val suggestions = repository.searchDestinations("   ", sampleVehicleFix)

        assertTrue(suggestions.size >= 5)
        // Verify ascending distance ordering
        for (i in 0 until suggestions.size - 1) {
            val dist1 = suggestions[i].distanceMeters ?: Float.MAX_VALUE
            val dist2 = suggestions[i + 1].distanceMeters ?: Float.MAX_VALUE
            assertTrue(dist1 <= dist2)
        }
    }

    @Test
    fun `searchDestinations with non-matching query returns empty list`() = runBlocking {
        val results = repository.searchDestinations("nonexistentdestinationxyz", sampleVehicleFix)
        assertTrue(results.isEmpty())
    }
}

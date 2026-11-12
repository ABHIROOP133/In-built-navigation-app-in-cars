package com.autonav.data

import com.autonav.data.model.Destination
import com.autonav.data.model.DestinationCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class DestinationTest {

    @Test
    fun `formattedDistance produces clean automotive distance strings`() {
        val destNoDistance = Destination(
            id = "1",
            title = "Test Place",
            subtitle = "Test Address",
            latitude = 12.0,
            longitude = 77.0,
            distanceMeters = null
        )
        assertEquals("", destNoDistance.formattedDistance)

        val destMeters = destNoDistance.copy(distanceMeters = 650.4f)
        assertEquals("650 m", destMeters.formattedDistance)

        val destKm = destNoDistance.copy(distanceMeters = 4250f)
        assertEquals("4.3 km", destKm.formattedDistance)

        val destLongKm = destNoDistance.copy(distanceMeters = 27800f)
        assertEquals("27.8 km", destLongKm.formattedDistance)
    }

    @Test
    fun `destination category display labels match specification`() {
        assertEquals("EV Charging", DestinationCategory.CHARGING_STATION.displayLabel)
        assertEquals("Fuel Station", DestinationCategory.FUEL.displayLabel)
        assertEquals("Airport", DestinationCategory.AIRPORT.displayLabel)
        assertEquals("Transit Station", DestinationCategory.TRANSIT.displayLabel)
    }
}


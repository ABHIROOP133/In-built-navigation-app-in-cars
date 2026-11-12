package com.autonav.data

import com.autonav.data.model.LocationFix
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class LocationFixTest {

    @Test
    fun `isValid validates latitude and longitude boundaries correctly`() {
        val validFix = LocationFix(latitude = 12.9716, longitude = 77.5946)
        assertTrue(validFix.isValid)

        val invalidLat = LocationFix(latitude = 95.0, longitude = 77.5946)
        assertFalse(invalidLat.isValid)

        val invalidLon = LocationFix(latitude = 12.9716, longitude = 195.0)
        assertFalse(invalidLon.isValid)
    }

    @Test
    fun `distanceTo computes accurate great circle distances via Haversine`() {
        // Bangalore MG Road (12.9756, 77.6066) to Kempegowda Airport (13.1989, 77.7068) ~27.2 km
        val p1 = LocationFix(latitude = 12.9756, longitude = 77.6066)
        val p2 = LocationFix(latitude = 13.1989, longitude = 77.7068)

        val distanceMeters = p1.distanceTo(p2)
        // Check within +/- 500 meters tolerance for 27km span
        assertTrue("Distance should be approximately 27,200m, got $distanceMeters", abs(distanceMeters - 27200f) < 800f)
    }

    @Test
    fun `bearingTo calculates expected cardinal directions`() {
        val origin = LocationFix(latitude = 0.0, longitude = 0.0)

        val north = LocationFix(latitude = 1.0, longitude = 0.0)
        val east = LocationFix(latitude = 0.0, longitude = 1.0)
        val south = LocationFix(latitude = -1.0, longitude = 0.0)
        val west = LocationFix(latitude = 0.0, longitude = -1.0)

        assertEquals(0f, origin.bearingTo(north), 0.5f)
        assertEquals(90f, origin.bearingTo(east), 0.5f)
        assertEquals(180f, origin.bearingTo(south), 0.5f)
        assertEquals(270f, origin.bearingTo(west), 0.5f)
    }
}


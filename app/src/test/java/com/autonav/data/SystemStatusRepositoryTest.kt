package com.autonav.data

import com.autonav.data.model.ApplicationStatus
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.LocationFix
import com.autonav.data.model.NavigationStatus
import com.autonav.data.repository.DefaultSystemStatusRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SystemStatusRepositoryTest {

    @Test
    fun `initial system status reports truthful disconnected and inactive states`() {
        val repository = DefaultSystemStatusRepository()
        val snapshot = repository.getInitialStatus()

        assertEquals(GpsConnectionStatus.NOT_CONNECTED, snapshot.gpsStatus)
        assertEquals(NavigationStatus.INACTIVE, snapshot.navigationStatus)
        assertEquals(ApplicationStatus.READY, snapshot.applicationStatus)
        assertNull(snapshot.currentSpeedKmh)
        assertNull(snapshot.lastKnownLocation)
        assertEquals("-- km/h", snapshot.formattedSpeed)
    }

    @Test
    fun `formatted status labels match specification requirements`() {
        val repository = DefaultSystemStatusRepository()
        val snapshot = repository.systemStatus.value

        assertEquals("GPS status: Not connected", snapshot.formattedGpsStatus)
        assertEquals("Navigation status: Inactive", snapshot.formattedNavigationStatus)
        assertEquals("Application status: Ready", snapshot.formattedApplicationStatus)
        assertEquals("-- km/h", snapshot.formattedSpeed)
    }

    @Test
    fun `updateLocationFix updates speed, location and transitions GPS to CONNECTED`() {
        val repository = DefaultSystemStatusRepository()
        val fix = LocationFix(
            latitude = 12.9716,
            longitude = 77.5946,
            speedKmh = 48.5f,
            bearing = 90f
        )

        repository.updateLocationFix(fix)
        val snapshot = repository.systemStatus.value

        assertEquals(GpsConnectionStatus.CONNECTED, snapshot.gpsStatus)
        assertEquals(48.5f, snapshot.currentSpeedKmh)
        assertEquals(fix, snapshot.lastKnownLocation)
        assertEquals("49 km/h", snapshot.formattedSpeed)
        assertEquals("GPS status: Connected", snapshot.formattedGpsStatus)
    }

    @Test
    fun `updateGpsStatus to NOT_CONNECTED clears active speed telemetry`() {
        val repository = DefaultSystemStatusRepository()
        val fix = LocationFix(latitude = 12.0, longitude = 77.0, speedKmh = 60f)
        repository.updateLocationFix(fix)

        // GPS disconnected (e.g. entering underground tunnel with lost lock)
        repository.updateGpsStatus(GpsConnectionStatus.NOT_CONNECTED, 0)
        val snapshot = repository.systemStatus.value

        assertEquals(GpsConnectionStatus.NOT_CONNECTED, snapshot.gpsStatus)
        assertNull(snapshot.currentSpeedKmh)
        assertEquals("-- km/h", snapshot.formattedSpeed)
    }
}

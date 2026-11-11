package com.autonav.data

import com.autonav.data.model.ApplicationStatus
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.NavigationStatus
import com.autonav.data.repository.DefaultSystemStatusRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class SystemStatusRepositoryTest {

    @Test
    fun `initial system status reports truthful Day 1 disconnected and inactive states`() {
        val repository = DefaultSystemStatusRepository()
        val snapshot = repository.getInitialStatus()

        assertEquals(GpsConnectionStatus.NOT_CONNECTED, snapshot.gpsStatus)
        assertEquals(NavigationStatus.INACTIVE, snapshot.navigationStatus)
        assertEquals(ApplicationStatus.READY, snapshot.applicationStatus)
    }

    @Test
    fun `formatted status labels match Day 1 specification requirements`() {
        val repository = DefaultSystemStatusRepository()
        val snapshot = repository.systemStatus.value

        assertEquals("GPS status: Not connected", snapshot.formattedGpsStatus)
        assertEquals("Navigation status: Inactive", snapshot.formattedNavigationStatus)
        assertEquals("Application status: Ready", snapshot.formattedApplicationStatus)
    }
}

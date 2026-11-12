package com.autonav.viewmodel

import com.autonav.data.model.ApplicationStatus
import com.autonav.data.model.Destination
import com.autonav.data.model.DestinationCategory
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.LocationFix
import com.autonav.data.model.NavigationStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    @Test
    fun `initial state contains empty search query and baseline system status`() {
        val viewModel = HomeViewModel()
        val state = viewModel.uiState.value

        assertEquals("", state.searchQuery)
        assertNull(state.pendingFeatureMessage)
        assertFalse(state.isSettingsDialogVisible)
        assertEquals(GpsConnectionStatus.NOT_CONNECTED, state.systemStatus.gpsStatus)
        assertEquals(NavigationStatus.INACTIVE, state.systemStatus.navigationStatus)
        assertEquals(ApplicationStatus.READY, state.systemStatus.applicationStatus)
        assertFalse(state.hasLocationPermission)
        assertFalse(state.hasValidApiKey)
        assertTrue(state.isFollowingVehicle)
        assertTrue(state.searchResults.isEmpty())
        assertFalse(state.isSearchOverlayVisible)
        assertNull(state.selectedDestination)
    }

    @Test
    fun `onLocationPermissionChanged updates permission state`() {
        val viewModel = HomeViewModel()

        viewModel.onLocationPermissionChanged(true)
        assertTrue(viewModel.uiState.value.hasLocationPermission)

        viewModel.onLocationPermissionChanged(false)
        assertFalse(viewModel.uiState.value.hasLocationPermission)
    }

    @Test
    fun `onApiKeyStatusChanged updates valid API key state`() {
        val viewModel = HomeViewModel()

        viewModel.onApiKeyStatusChanged(true)
        assertTrue(viewModel.uiState.value.hasValidApiKey)

        viewModel.onApiKeyStatusChanged(false)
        assertFalse(viewModel.uiState.value.hasValidApiKey)
    }

    @Test
    fun `recenter and pan map toggles vehicle follow posture`() {
        val viewModel = HomeViewModel()
        assertTrue(viewModel.uiState.value.isFollowingVehicle)

        viewModel.onUserPanMap()
        assertFalse(viewModel.uiState.value.isFollowingVehicle)

        viewModel.onRecenterMap()
        assertTrue(viewModel.uiState.value.isFollowingVehicle)
    }

    @Test
    fun `onLocationFixReceived propagates location and speed to state`() {
        val testScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Unconfined)
        val viewModel = HomeViewModel(scope = testScope)

        val fix = LocationFix(
            latitude = 12.9716,
            longitude = 77.5946,
            speedKmh = 52.0f,
            bearing = 180f
        )

        viewModel.onLocationFixReceived(fix)

        val state = viewModel.uiState.value
        assertEquals(GpsConnectionStatus.CONNECTED, state.systemStatus.gpsStatus)
        assertEquals(52.0f, state.systemStatus.currentSpeedKmh)
        assertEquals("52 km/h", state.systemStatus.formattedSpeed)
        assertEquals(fix, state.lastKnownLocation)
    }

    @Test
    fun `onSearchQueryChanged with 2 or more characters triggers search and displays overlay`() {
        val viewModel = HomeViewModel()

        viewModel.onSearchQueryChanged("Airport")

        val state = viewModel.uiState.value
        assertEquals("Airport", state.searchQuery)
        assertTrue(state.searchResults.isNotEmpty())
        assertTrue(state.isSearchOverlayVisible)
        assertTrue(state.searchResults.any { it.title.contains("Airport") })
    }

    @Test
    fun `onSearchSubmitted with query populates results and displays overlay`() {
        val viewModel = HomeViewModel()
        viewModel.onSearchQueryChanged("Electronic")

        viewModel.onSearchSubmitted()

        val state = viewModel.uiState.value
        assertTrue(state.searchResults.isNotEmpty())
        assertTrue(state.isSearchOverlayVisible)
    }

    @Test
    fun `onSearchSubmitted with blank query loads suggested destinations`() {
        val viewModel = HomeViewModel()
        viewModel.onSearchQueryChanged("   ")

        viewModel.onSearchSubmitted()

        val state = viewModel.uiState.value
        assertTrue(state.searchResults.isNotEmpty())
        assertTrue(state.isSearchOverlayVisible)
    }

    @Test
    fun `onDestinationSelected selects waypoint, sets title, hides overlay, and unfocuses vehicle follow`() {
        val viewModel = HomeViewModel()
        val dest = Destination(
            id = "test_dest",
            title = "Kempegowda International Airport",
            subtitle = "Devanahalli",
            latitude = 13.1986,
            longitude = 77.7066,
            category = DestinationCategory.AIRPORT
        )

        viewModel.onDestinationSelected(dest)

        val state = viewModel.uiState.value
        assertEquals(dest, state.selectedDestination)
        assertEquals("Kempegowda International Airport", state.searchQuery)
        assertFalse(state.isSearchOverlayVisible)
        assertFalse(state.isFollowingVehicle)
    }

    @Test
    fun `onClearDestination resets selected waypoint, clears search, and resumes vehicle follow`() {
        val viewModel = HomeViewModel()
        val dest = Destination(
            id = "test_dest",
            title = "Station",
            subtitle = "City Centre",
            latitude = 12.0,
            longitude = 77.0
        )
        viewModel.onDestinationSelected(dest)

        viewModel.onClearDestination()

        val state = viewModel.uiState.value
        assertNull(state.selectedDestination)
        assertEquals("", state.searchQuery)
        assertTrue(state.isFollowingVehicle)
    }

    @Test
    fun `onClearSearch resets query and closes results overlay`() {
        val viewModel = HomeViewModel()
        viewModel.onSearchQueryChanged("Railway")
        viewModel.onSearchSubmitted()

        viewModel.onClearSearch()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertTrue(state.searchResults.isEmpty())
        assertFalse(state.isSearchOverlayVisible)
    }

    @Test
    fun `settings dialog visibility toggles cleanly`() {
        val viewModel = HomeViewModel()

        viewModel.onOpenSettings()
        assertTrue(viewModel.uiState.value.isSettingsDialogVisible)

        viewModel.onDismissSettings()
        assertFalse(viewModel.uiState.value.isSettingsDialogVisible)
    }
}

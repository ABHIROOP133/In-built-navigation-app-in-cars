package com.autonav.viewmodel

import com.autonav.data.model.ApplicationStatus
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
    fun `onSearchQueryChanged updates query and strips newline characters`() {
        val viewModel = HomeViewModel()

        viewModel.onSearchQueryChanged("Central Railway Station\n")

        assertEquals("Central Railway Station", viewModel.uiState.value.searchQuery)
    }

    @Test
    fun `onSearchSubmitted with valid destination sets explicit pending milestone message`() {
        val viewModel = HomeViewModel()
        viewModel.onSearchQueryChanged(" Kempegowda International Airport ")

        viewModel.onSearchSubmitted()

        val state = viewModel.uiState.value
        assertEquals("Kempegowda International Airport", state.searchQuery)
        assertNotNull(state.pendingFeatureMessage)
        assertTrue(state.pendingFeatureMessage!!.contains("Kempegowda International Airport"))
        assertTrue(state.pendingFeatureMessage.contains("Pending Milestone 3"))
    }

    @Test
    fun `onSearchSubmitted with blank query displays guidance notice`() {
        val viewModel = HomeViewModel()
        viewModel.onSearchQueryChanged("   ")

        viewModel.onSearchSubmitted()

        val state = viewModel.uiState.value
        assertNotNull(state.pendingFeatureMessage)
        assertTrue(state.pendingFeatureMessage!!.contains("Milestone 3"))
    }

    @Test
    fun `onClearSearch resets query and clears pending notice`() {
        val viewModel = HomeViewModel()
        viewModel.onSearchQueryChanged("Tech Park")
        viewModel.onSearchSubmitted()

        viewModel.onClearSearch()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertNull(state.pendingFeatureMessage)
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

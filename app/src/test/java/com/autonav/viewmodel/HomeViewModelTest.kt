package com.autonav.viewmodel

import com.autonav.data.model.ApplicationStatus
import com.autonav.data.model.GpsConnectionStatus
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

package com.autonav.viewmodel

import com.autonav.data.model.Destination
import com.autonav.data.model.LocationFix
import com.autonav.data.model.SystemStatusSnapshot

/**
 * Immutable UI state for the AutoNav Home Screen.
 *
 * Captures real-time vehicle telemetry, map camera follow posture, permission state,
 * search queries, search results, selected destination, and truthful system status.
 */
data class HomeUiState(
    val searchQuery: String = "",
    val pendingFeatureMessage: String? = null,
    val isSettingsDialogVisible: Boolean = false,
    val systemStatus: SystemStatusSnapshot = SystemStatusSnapshot(),
    val hasLocationPermission: Boolean = false,
    val hasValidApiKey: Boolean = false,
    val isFollowingVehicle: Boolean = true,
    val lastKnownLocation: LocationFix? = null,
    val searchResults: List<Destination> = emptyList(),
    val isSearching: Boolean = false,
    val isSearchOverlayVisible: Boolean = false,
    val selectedDestination: Destination? = null
)

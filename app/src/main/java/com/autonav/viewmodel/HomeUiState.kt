package com.autonav.viewmodel

import com.autonav.data.model.SystemStatusSnapshot

/**
 * Immutable UI state for the AutoNav Home Screen.
 *
 * All fields represent truthful Day 1 system states; unimplemented features (map rendering,
 * geocoding search, GPS telemetry, and system configuration) are explicitly flagged.
 */
data class HomeUiState(
    val searchQuery: String = "",
    val pendingFeatureMessage: String? = null,
    val isSettingsDialogVisible: Boolean = false,
    val systemStatus: SystemStatusSnapshot = SystemStatusSnapshot()
)

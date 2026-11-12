package com.autonav.viewmodel

import androidx.lifecycle.ViewModel
import com.autonav.data.model.Destination
import com.autonav.data.model.LocationFix
import com.autonav.data.repository.DefaultDestinationRepository
import com.autonav.data.repository.DefaultSystemStatusRepository
import com.autonav.data.repository.DestinationRepository
import com.autonav.data.repository.SystemStatusRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel managing state and user interactions for the AutoNav Home Screen.
 * Coordinates system telemetry, geocoding search, and map destination selection.
 */
class HomeViewModel(
    private val statusRepository: SystemStatusRepository = DefaultSystemStatusRepository(),
    private val destinationRepository: DestinationRepository = DefaultDestinationRepository(),
    scope: CoroutineScope? = null
) : ViewModel() {

    private val viewModelScope: CoroutineScope = scope ?: CoroutineScope(Dispatchers.Unconfined)

    private val _uiState = MutableStateFlow(
        HomeUiState(
            systemStatus = statusRepository.getInitialStatus(),
            lastKnownLocation = statusRepository.getInitialStatus().lastKnownLocation
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            statusRepository.systemStatus.collectLatest { newStatus ->
                _uiState.update { current ->
                    current.copy(
                        systemStatus = newStatus,
                        lastKnownLocation = newStatus.lastKnownLocation
                    )
                }
            }
        }
    }

    fun onLocationPermissionChanged(isGranted: Boolean) {
        _uiState.update { current ->
            current.copy(hasLocationPermission = isGranted)
        }
    }

    fun onApiKeyStatusChanged(isValid: Boolean) {
        _uiState.update { current ->
            current.copy(hasValidApiKey = isValid)
        }
    }

    fun onRecenterMap() {
        _uiState.update { current ->
            current.copy(isFollowingVehicle = true)
        }
    }

    fun onUserPanMap() {
        _uiState.update { current ->
            current.copy(isFollowingVehicle = false)
        }
    }

    fun onLocationFixReceived(fix: LocationFix) {
        statusRepository.updateLocationFix(fix)
    }

    fun onSearchQueryChanged(newQuery: String) {
        val sanitizedQuery = newQuery.replace("\n", "").replace("\r", "")
        _uiState.update { current ->
            current.copy(
                searchQuery = sanitizedQuery,
                pendingFeatureMessage = null
            )
        }

        if (sanitizedQuery.trim().length >= 2) {
            viewModelScope.launch {
                val results = destinationRepository.searchDestinations(
                    query = sanitizedQuery,
                    currentFix = _uiState.value.lastKnownLocation
                )
                _uiState.update { current ->
                    current.copy(
                        searchResults = results,
                        isSearchOverlayVisible = results.isNotEmpty()
                    )
                }
            }
        } else if (sanitizedQuery.isBlank()) {
            _uiState.update { current ->
                current.copy(
                    searchResults = emptyList(),
                    isSearchOverlayVisible = false
                )
            }
        }
    }

    fun onSearchSubmitted() {
        val trimmedQuery = _uiState.value.searchQuery.trim()
        if (trimmedQuery.isEmpty()) {
            viewModelScope.launch {
                val suggestions = destinationRepository.getSuggestedDestinations(
                    currentFix = _uiState.value.lastKnownLocation
                )
                _uiState.update { current ->
                    current.copy(
                        searchResults = suggestions,
                        isSearchOverlayVisible = suggestions.isNotEmpty(),
                        pendingFeatureMessage = if (suggestions.isEmpty()) "No suggested destinations available." else null
                    )
                }
            }
            return
        }

        _uiState.update { it.copy(isSearching = true) }
        viewModelScope.launch {
            val results = destinationRepository.searchDestinations(
                query = trimmedQuery,
                currentFix = _uiState.value.lastKnownLocation
            )
            _uiState.update { current ->
                current.copy(
                    searchResults = results,
                    isSearching = false,
                    isSearchOverlayVisible = true,
                    pendingFeatureMessage = if (results.isEmpty()) "No destinations found for \"$trimmedQuery\"." else null
                )
            }
        }
    }

    fun onDestinationSelected(destination: Destination) {
        _uiState.update { current ->
            current.copy(
                selectedDestination = destination,
                searchQuery = destination.title,
                isSearchOverlayVisible = false,
                isFollowingVehicle = false,
                pendingFeatureMessage = null
            )
        }
    }

    fun onClearDestination() {
        _uiState.update { current ->
            current.copy(
                selectedDestination = null,
                searchQuery = "",
                isFollowingVehicle = true,
                isSearchOverlayVisible = false,
                pendingFeatureMessage = null
            )
        }
    }

    fun onClearSearch() {
        _uiState.update { current ->
            current.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isSearchOverlayVisible = false,
                pendingFeatureMessage = null
            )
        }
    }

    fun onDismissSearchOverlay() {
        _uiState.update { current ->
            current.copy(isSearchOverlayVisible = false)
        }
    }

    fun onDismissPendingNotice() {
        _uiState.update { current ->
            current.copy(pendingFeatureMessage = null)
        }
    }

    fun onOpenSettings() {
        _uiState.update { current ->
            current.copy(isSettingsDialogVisible = true)
        }
    }

    fun onDismissSettings() {
        _uiState.update { current ->
            current.copy(isSettingsDialogVisible = false)
        }
    }
}

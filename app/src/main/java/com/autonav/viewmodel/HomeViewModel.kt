package com.autonav.viewmodel

import androidx.lifecycle.ViewModel
import com.autonav.data.repository.DefaultSystemStatusRepository
import com.autonav.data.repository.SystemStatusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel managing state and user interactions for the AutoNav Home Screen.
 */
class HomeViewModel(
    private val statusRepository: SystemStatusRepository = DefaultSystemStatusRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(systemStatus = statusRepository.getInitialStatus())
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(newQuery: String) {
        val sanitizedQuery = newQuery.replace("\n", "").replace("\r", "")
        _uiState.update { current ->
            current.copy(
                searchQuery = sanitizedQuery,
                pendingFeatureMessage = if (sanitizedQuery.isBlank()) null else current.pendingFeatureMessage
            )
        }
    }

    fun onSearchSubmitted() {
        val trimmedQuery = _uiState.value.searchQuery.trim()
        if (trimmedQuery.isEmpty()) {
            _uiState.update { current ->
                current.copy(
                    pendingFeatureMessage = "Enter a destination query. Geocoding and route search are scheduled for Milestone 3."
                )
            }
            return
        }

        _uiState.update { current ->
            current.copy(
                searchQuery = trimmedQuery,
                pendingFeatureMessage = "Destination search for \"$trimmedQuery\" is not yet active (Pending Milestone 3 geocoding integration)."
            )
        }
    }

    fun onClearSearch() {
        _uiState.update { current ->
            current.copy(
                searchQuery = "",
                pendingFeatureMessage = null
            )
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

package com.autonav.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autonav.BuildConfig
import com.autonav.R
import com.autonav.data.model.Destination
import com.autonav.maps.AutoNavMapView
import com.autonav.ui.components.BottomStatusBar
import com.autonav.ui.components.DestinationCard
import com.autonav.ui.components.DestinationSearchBar
import com.autonav.ui.components.SearchResultsOverlay
import com.autonav.ui.components.TopHeaderBar
import com.autonav.ui.theme.AutoNavTheme
import com.autonav.ui.theme.CharcoalBackground
import com.autonav.ui.theme.CharcoalSurfaceElevated
import com.autonav.ui.theme.SteelBlueAccent
import com.autonav.ui.theme.StructuralBorder
import com.autonav.ui.theme.TextPrimary
import com.autonav.ui.theme.TextSecondary
import com.autonav.viewmodel.HomeUiState
import com.autonav.viewmodel.HomeViewModel

/**
 * Stateful entry point for the AutoNav Home Screen connected to [HomeViewModel].
 * Handles runtime location permission handshakes, Maps API key validation, and destination search.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        viewModel.onLocationPermissionChanged(fineGranted || coarseGranted)
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.onLocationPermissionChanged(hasFine || hasCoarse)

        val hasValidKey = try {
            BuildConfig.MAPS_API_KEY.isNotBlank()
        } catch (e: Exception) {
            false
        }
        viewModel.onApiKeyStatusChanged(hasValidKey)
    }

    HomeScreenContent(
        uiState = uiState,
        onRequestPermission = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        },
        onRecenterClick = viewModel::onRecenterMap,
        onUserPanMap = viewModel::onUserPanMap,
        onSearchQueryChange = viewModel::onSearchQueryChanged,
        onSearchSubmit = viewModel::onSearchSubmitted,
        onClearSearch = viewModel::onClearSearch,
        onSelectDestination = viewModel::onDestinationSelected,
        onClearDestination = viewModel::onClearDestination,
        onDismissSearchOverlay = viewModel::onDismissSearchOverlay,
        onStartRoute = {
            // Prepared for Milestone 4 (Route Calculation & Turn-by-Turn Engine)
        },
        onDismissPendingNotice = viewModel::onDismissPendingNotice,
        onOpenSettings = viewModel::onOpenSettings,
        onDismissSettings = viewModel::onDismissSettings,
        modifier = modifier
    )
}

/**
 * Stateless, responsive Home Screen composable designed for automotive touchscreens.
 */
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onRequestPermission: () -> Unit,
    onRecenterClick: () -> Unit,
    onUserPanMap: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearSearch: () -> Unit,
    onSelectDestination: (Destination) -> Unit,
    onClearDestination: () -> Unit,
    onDismissSearchOverlay: () -> Unit,
    onStartRoute: (Destination) -> Unit,
    onDismissPendingNotice: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground)
            .testTag("home_screen_root"),
        color = CharcoalBackground
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isCompactWidth = maxWidth < 680.dp
            val isShortHeight = maxHeight < 520.dp
            val horizontalPadding = if (maxWidth >= 1200.dp) 24.dp else 16.dp
            val verticalSpacing = if (isShortHeight) 8.dp else 12.dp

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // A. Header Area
                TopHeaderBar(
                    onSettingsClick = onOpenSettings,
                    showExtendedSubtitle = !isCompactWidth && !isShortHeight
                )

                // Main Content Workspace (Search + Map Viewport + Bottom Status Bar)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            horizontal = horizontalPadding,
                            vertical = verticalSpacing
                        ),
                    verticalArrangement = Arrangement.spacedBy(verticalSpacing)
                ) {
                    // C. Destination Search Area
                    DestinationSearchBar(
                        query = uiState.searchQuery,
                        pendingFeatureMessage = uiState.pendingFeatureMessage,
                        onQueryChange = onSearchQueryChange,
                        onSearchSubmit = onSearchSubmit,
                        onClearQuery = onClearSearch,
                        onDismissPendingMessage = onDismissPendingNotice,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // B. Main Automotive Map Viewport with Floating Overlays
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        AutoNavMapView(
                            currentLocation = uiState.lastKnownLocation,
                            selectedDestination = uiState.selectedDestination,
                            gpsStatus = uiState.systemStatus.gpsStatus,
                            hasLocationPermission = uiState.hasLocationPermission,
                            hasValidApiKey = uiState.hasValidApiKey,
                            isFollowingVehicle = uiState.isFollowingVehicle,
                            onRequestPermission = onRequestPermission,
                            onRecenterClick = onRecenterClick,
                            onUserPanMap = onUserPanMap,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Search Results Overlay
                        if (uiState.isSearchOverlayVisible && uiState.searchResults.isNotEmpty()) {
                            SearchResultsOverlay(
                                results = uiState.searchResults,
                                onSelectDestination = onSelectDestination,
                                onDismiss = onDismissSearchOverlay,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }

                        // Floating Selected Destination Preview Card
                        if (uiState.selectedDestination != null && !uiState.isSearchOverlayVisible) {
                            DestinationCard(
                                destination = uiState.selectedDestination,
                                onStartRoute = onStartRoute,
                                onClearDestination = onClearDestination,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                                    .fillMaxWidth(if (isCompactWidth) 1f else 0.55f)
                            )
                        }
                    }

                    // D. Bottom System Telemetry Area (GPS status, Vehicle Speed, Nav status, App status)
                    BottomStatusBar(
                        systemStatus = uiState.systemStatus,
                        isCompactLayout = isCompactWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (uiState.isSettingsDialogVisible) {
                SettingsPendingDialog(onDismiss = onDismissSettings)
            }
        }
    }
}

@Composable
private fun SettingsPendingDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .border(1.dp, StructuralBorder, RoundedCornerShape(4.dp))
            .testTag("settings_pending_dialog"),
        shape = RoundedCornerShape(4.dp),
        containerColor = CharcoalSurfaceElevated,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(id = R.string.settings_dialog_title),
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.settings_dialog_message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.settings_dialog_close),
                    style = MaterialTheme.typography.labelLarge,
                    color = SteelBlueAccent
                )
            }
        }
    )
}

@Preview(
    name = "Automotive Standard Head Unit (1280x720)",
    widthDp = 1280,
    heightDp = 720,
    showBackground = true
)
@Composable
private fun HomeScreenStandardAutomotivePreview() {
    AutoNavTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            onRequestPermission = {},
            onRecenterClick = {},
            onUserPanMap = {},
            onSearchQueryChange = {},
            onSearchSubmit = {},
            onClearSearch = {},
            onSelectDestination = {},
            onClearDestination = {},
            onDismissSearchOverlay = {},
            onStartRoute = {},
            onDismissPendingNotice = {},
            onOpenSettings = {},
            onDismissSettings = {}
        )
    }
}

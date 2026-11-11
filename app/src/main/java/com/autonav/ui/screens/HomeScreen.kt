package com.autonav.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autonav.R
import com.autonav.ui.components.BottomStatusBar
import com.autonav.ui.components.DestinationSearchBar
import com.autonav.ui.components.MapPlaceholderView
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
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChanged,
        onSearchSubmit = viewModel::onSearchSubmitted,
        onClearSearch = viewModel::onClearSearch,
        onDismissPendingNotice = viewModel::onDismissPendingNotice,
        onOpenSettings = viewModel::onOpenSettings,
        onDismissSettings = viewModel::onDismissSettings,
        modifier = modifier
    )
}

/**
 * Stateless, responsive Home Screen composable designed for automotive touchscreens.
 *
 * Adapts spacing and layout posture across compact 7-inch displays (800x480),
 * standard 10–12 inch head units (1280x720), ultra-wide cockpit displays (1920x720),
 * and portrait tablet orientations without introducing vertical scrolling.
 */
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearSearch: () -> Unit,
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

                    // B. Main Map Viewport Placeholder (occupies all remaining vertical space)
                    MapPlaceholderView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    // D. Bottom System Telemetry Area
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
            onSearchQueryChange = {},
            onSearchSubmit = {},
            onClearSearch = {},
            onDismissPendingNotice = {},
            onOpenSettings = {},
            onDismissSettings = {}
        )
    }
}

@Preview(
    name = "Automotive Compact 7-inch Display (800x480)",
    widthDp = 800,
    heightDp = 480,
    showBackground = true
)
@Composable
private fun HomeScreenCompactAutomotivePreview() {
    AutoNavTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            onSearchQueryChange = {},
            onSearchSubmit = {},
            onClearSearch = {},
            onDismissPendingNotice = {},
            onOpenSettings = {},
            onDismissSettings = {}
        )
    }
}

@Preview(
    name = "Automotive Ultra-Wide Cockpit (1920x720)",
    widthDp = 1920,
    heightDp = 720,
    showBackground = true
)
@Composable
private fun HomeScreenUltraWideAutomotivePreview() {
    AutoNavTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            onSearchQueryChange = {},
            onSearchSubmit = {},
            onClearSearch = {},
            onDismissPendingNotice = {},
            onOpenSettings = {},
            onDismissSettings = {}
        )
    }
}

@Preview(
    name = "Tablet Portrait Fallback (600x960)",
    widthDp = 600,
    heightDp = 960,
    showBackground = true
)
@Composable
private fun HomeScreenPortraitFallbackPreview() {
    AutoNavTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            onSearchQueryChange = {},
            onSearchSubmit = {},
            onClearSearch = {},
            onDismissPendingNotice = {},
            onOpenSettings = {},
            onDismissSettings = {}
        )
    }
}

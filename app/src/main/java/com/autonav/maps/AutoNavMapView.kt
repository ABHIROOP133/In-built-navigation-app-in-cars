package com.autonav.maps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.LocationFix
import com.autonav.ui.theme.CharcoalBackground
import com.autonav.ui.theme.CharcoalSurface
import com.autonav.ui.theme.CharcoalSurfaceElevated
import com.autonav.ui.theme.GridHairline
import com.autonav.ui.theme.SteelBlueAccent
import com.autonav.ui.theme.StructuralBorder
import com.autonav.ui.theme.TextMuted
import com.autonav.ui.theme.TextPrimary
import com.autonav.ui.theme.TextSecondary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * Automotive Map Viewport featuring Google Maps Platform integration, vehicle tracking,
 * dark low-glare styling, and a fallback viewport when permissions or API keys are pending.
 */
@Composable
fun AutoNavMapView(
    currentLocation: LocationFix?,
    gpsStatus: GpsConnectionStatus,
    hasLocationPermission: Boolean,
    hasValidApiKey: Boolean,
    isFollowingVehicle: Boolean,
    onRequestPermission: () -> Unit,
    onRecenterClick: () -> Unit,
    onUserPanMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CharcoalBackground, RoundedCornerShape(4.dp))
            .border(width = 1.dp, color = StructuralBorder, shape = RoundedCornerShape(4.dp))
            .testTag("autonav_map_container")
    ) {
        if (!hasValidApiKey || !hasLocationPermission) {
            // Fallback screen explaining missing requirements without crash
            MapConfigurationFallback(
                hasLocationPermission = hasLocationPermission,
                hasValidApiKey = hasValidApiKey,
                onRequestPermission = onRequestPermission
            )
        } else {
            // Live Google Map Viewport
            val defaultCoordinate = LatLng(12.9716, 77.5946) // Default fallback center
            val carLatLng = remember(currentLocation) {
                currentLocation?.let { LatLng(it.latitude, it.longitude) } ?: defaultCoordinate
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(carLatLng, 16f)
            }

            // Detect user camera manipulation to exit follow mode
            LaunchedEffect(cameraPositionState.isMoving) {
                if (cameraPositionState.isMoving && cameraPositionState.cameraMoveStartedReason.value == 1) { // 1 = GESTURE
                    onUserPanMap()
                }
            }

            // Smoothly track vehicle when following
            LaunchedEffect(carLatLng, isFollowingVehicle) {
                if (isFollowingVehicle && currentLocation != null) {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(carLatLng)
                                .zoom(16.5f)
                                .bearing(currentLocation.bearing ?: cameraPositionState.position.bearing)
                                .tilt(40f) // Automotive navigation tilt
                                .build()
                        ),
                        800
                    )
                }
            }

            val mapProperties = remember {
                MapProperties(
                    mapStyleOptions = MapStyleOptions(AutomotiveMapTheme.MAP_STYLE_AUTOMOTIVE_DARK),
                    isMyLocationEnabled = false // Custom vehicle reticle rendered via MarkerComposable
                )
            }

            val mapUiSettings = remember {
                MapUiSettings(
                    compassEnabled = true,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    tiltGesturesEnabled = true,
                    rotationGesturesEnabled = true
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("google_map_viewport"),
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                uiSettings = mapUiSettings
            ) {
                if (currentLocation != null) {
                    val markerState = rememberMarkerState(position = carLatLng)
                    LaunchedEffect(carLatLng) {
                        markerState.position = carLatLng
                    }

                    MarkerComposable(
                        state = markerState,
                        title = "AutoNav Vehicle",
                        anchor = Offset(0.5f, 0.5f)
                    ) {
                        VehicleLocationReticle(bearing = currentLocation.bearing)
                    }
                }
            }

            // Floating "Re-center on Vehicle" touch button
            if (!isFollowingVehicle && currentLocation != null) {
                IconButton(
                    onClick = onRecenterClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                        .size(56.dp)
                        .background(CharcoalSurfaceElevated, CircleShape)
                        .border(1.dp, SteelBlueAccent, CircleShape)
                        .testTag("recenter_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Re-center vehicle tracking",
                        tint = SteelBlueAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * Automotive arrow reticle oriented toward vehicle heading.
 */
@Composable
fun VehicleLocationReticle(
    bearing: Float?,
    modifier: Modifier = Modifier
) {
    val rotationAngle = bearing ?: 0f

    Box(
        modifier = modifier
            .size(48.dp)
            .rotate(rotationAngle),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(36.dp)) {
            val w = size.width
            val h = size.height

            // Outer pulse circle
            drawCircle(
                color = SteelBlueAccent.copy(alpha = 0.25f),
                radius = w / 2f
            )

            // Dynamic Navigation Arrow
            val path = Path().apply {
                moveTo(w / 2f, 4f)
                lineTo(w - 6f, h - 6f)
                lineTo(w / 2f, h - 14f)
                lineTo(6f, h - 6f)
                close()
            }

            drawPath(path = path, color = SteelBlueAccent)
            drawCircle(color = TextPrimary, radius = 3.dp.toPx(), center = Offset(w / 2f, h / 2f))
        }
    }
}

/**
 * Restrained engineering fallback panel shown when location permission or API key is pending.
 */
@Composable
private fun MapConfigurationFallback(
    hasLocationPermission: Boolean,
    hasValidApiKey: Boolean,
    onRequestPermission: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Orthographic background grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepPx = 64.dp.toPx()
            val w = size.width
            val h = size.height

            var x = stepPx
            while (x < w) {
                drawLine(GridHairline, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                x += stepPx
            }
            var y = stepPx
            while (y < h) {
                drawLine(GridHairline, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                y += stepPx
            }
        }

        Column(
            modifier = Modifier
                .widthIn(max = 540.dp)
                .padding(24.dp)
                .background(CharcoalSurface, RoundedCornerShape(4.dp))
                .border(width = 1.dp, color = StructuralBorder, shape = RoundedCornerShape(4.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(CharcoalSurfaceElevated, RoundedCornerShape(2.dp))
                    .border(width = 1.dp, color = SteelBlueAccent, shape = RoundedCornerShape(2.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (!hasLocationPermission) "GPS PERMISSION REQUIRED" else "MAPS API KEY PENDING",
                    style = MaterialTheme.typography.labelMedium,
                    color = SteelBlueAccent
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (!hasLocationPermission) {
                    "Location Access Required for Automotive Navigation"
                } else {
                    "Google Maps SDK Configuration Required"
                },
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (!hasLocationPermission) {
                    "AutoNav requires device location permissions to connect to vehicle GNSS receivers, render current coordinates, and compute vehicle ground speed."
                } else {
                    "To activate the live Google Maps viewport, provide your Maps API key in local.properties:\n\nMAPS_API_KEY=your_key_here\n\nKeys are loaded securely into BuildConfig without committing to Git."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            if (!hasLocationPermission) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SteelBlueAccent,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.testTag("grant_permission_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Grant Location Permission",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}


package com.autonav.data.repository

import com.autonav.data.model.ApplicationStatus
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.LocationFix
import com.autonav.data.model.NavigationStatus
import com.autonav.data.model.SystemStatusSnapshot
import com.autonav.location.GpsLocationClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Contract for providing system telemetry and operational readiness state to the UI layer.
 */
interface SystemStatusRepository {
    val systemStatus: StateFlow<SystemStatusSnapshot>
    fun getInitialStatus(): SystemStatusSnapshot
    fun updateLocationFix(fix: LocationFix)
    fun updateGpsStatus(status: GpsConnectionStatus, satelliteCount: Int = 0)
    fun updateNavigationStatus(status: NavigationStatus)
    fun attachGpsClient(client: GpsLocationClient, scope: CoroutineScope)
}

/**
 * Milestone 2 implementation of [SystemStatusRepository].
 *
 * Truthfully streams hardware GPS status and vehicle ground speed to the UI layer.
 */
class DefaultSystemStatusRepository : SystemStatusRepository {

    private val initialSnapshot = SystemStatusSnapshot(
        gpsStatus = GpsConnectionStatus.NOT_CONNECTED,
        navigationStatus = NavigationStatus.INACTIVE,
        applicationStatus = ApplicationStatus.READY,
        currentSpeedKmh = null,
        lastKnownLocation = null,
        satelliteCount = 0
    )

    private val _systemStatus = MutableStateFlow(initialSnapshot)
    override val systemStatus: StateFlow<SystemStatusSnapshot> = _systemStatus.asStateFlow()

    override fun getInitialStatus(): SystemStatusSnapshot = initialSnapshot

    override fun updateLocationFix(fix: LocationFix) {
        _systemStatus.update { current ->
            current.copy(
                lastKnownLocation = fix,
                currentSpeedKmh = fix.speedKmh,
                gpsStatus = GpsConnectionStatus.CONNECTED
            )
        }
    }

    override fun updateGpsStatus(status: GpsConnectionStatus, satelliteCount: Int) {
        _systemStatus.update { current ->
            current.copy(
                gpsStatus = status,
                satelliteCount = satelliteCount,
                currentSpeedKmh = if (status == GpsConnectionStatus.CONNECTED) current.currentSpeedKmh else null
            )
        }
    }

    override fun updateNavigationStatus(status: NavigationStatus) {
        _systemStatus.update { current ->
            current.copy(navigationStatus = status)
        }
    }

    override fun attachGpsClient(client: GpsLocationClient, scope: CoroutineScope) {
        scope.launch {
            client.connectionStatus.collectLatest { status ->
                updateGpsStatus(status, client.satelliteCount.value)
            }
        }
        scope.launch {
            client.satelliteCount.collectLatest { count ->
                updateGpsStatus(client.connectionStatus.value, count)
            }
        }
        scope.launch {
            client.locationUpdates.collectLatest { fix ->
                updateLocationFix(fix)
            }
        }
    }
}

package com.autonav.data.repository

import com.autonav.data.model.ApplicationStatus
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.NavigationStatus
import com.autonav.data.model.SystemStatusSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Contract for providing system telemetry and operational readiness state to the UI layer.
 */
interface SystemStatusRepository {
    val systemStatus: StateFlow<SystemStatusSnapshot>
    fun getInitialStatus(): SystemStatusSnapshot
}

/**
 * Day 1 baseline implementation of [SystemStatusRepository].
 *
 * Reports truthful unimplemented states for hardware subsystems:
 * - GPS status: Not connected
 * - Navigation status: Inactive
 * - Application status: Ready
 */
class DefaultSystemStatusRepository : SystemStatusRepository {

    private val initialSnapshot = SystemStatusSnapshot(
        gpsStatus = GpsConnectionStatus.NOT_CONNECTED,
        navigationStatus = NavigationStatus.INACTIVE,
        applicationStatus = ApplicationStatus.READY
    )

    private val _systemStatus = MutableStateFlow(initialSnapshot)
    override val systemStatus: StateFlow<SystemStatusSnapshot> = _systemStatus.asStateFlow()

    override fun getInitialStatus(): SystemStatusSnapshot = initialSnapshot
}

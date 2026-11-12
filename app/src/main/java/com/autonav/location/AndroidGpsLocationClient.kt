package com.autonav.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.autonav.data.model.GpsConnectionStatus
import com.autonav.data.model.LocationFix
import com.autonav.utils.SpeedFormatter
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Android implementation of [GpsLocationClient] communicating directly with vehicle GNSS
 * hardware via [LocationManager] and [GnssStatus].
 *
 * Designed to operate standalone on embedded head units and aftermarket automotive systems
 * without hard dependency on Google Play Services daemons.
 */
class AndroidGpsLocationClient(
    private val context: Context,
    private val minTimeMs: Long = 1000L,
    private val minDistanceMeters: Float = 0f
) : GpsLocationClient {

    private val locationManager: LocationManager? by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    }

    private val _locationUpdates = MutableSharedFlow<LocationFix>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val locationUpdates: Flow<LocationFix> = _locationUpdates.asSharedFlow()

    private val _connectionStatus = MutableStateFlow(GpsConnectionStatus.NOT_CONNECTED)
    override val connectionStatus: StateFlow<GpsConnectionStatus> = _connectionStatus.asStateFlow()

    private val _satelliteCount = MutableStateFlow(0)
    override val satelliteCount: StateFlow<Int> = _satelliteCount.asStateFlow()

    private var isTracking = false

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val speedKmh = if (location.hasSpeed()) {
                SpeedFormatter.metersPerSecondToKmh(location.speed)
            } else {
                null
            }

            val fix = LocationFix(
                latitude = location.latitude,
                longitude = location.longitude,
                bearing = if (location.hasBearing()) location.bearing else null,
                speedKmh = speedKmh,
                accuracyMeters = if (location.hasAccuracy()) location.accuracy else null,
                altitudeMeters = if (location.hasAltitude()) location.altitude else null,
                timestampMillis = location.time
            )

            _connectionStatus.value = GpsConnectionStatus.CONNECTED
            _locationUpdates.tryEmit(fix)
        }

        override fun onProviderEnabled(provider: String) {
            if (provider == LocationManager.GPS_PROVIDER) {
                _connectionStatus.value = GpsConnectionStatus.SEARCHING
            }
        }

        override fun onProviderDisabled(provider: String) {
            if (provider == LocationManager.GPS_PROVIDER) {
                _connectionStatus.value = GpsConnectionStatus.NOT_CONNECTED
                _satelliteCount.value = 0
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            // Handled via GnssStatus callback on API 24+
        }
    }

    private val gnssStatusCallback = object : GnssStatus.Callback() {
        override fun onSatelliteStatusChanged(status: GnssStatus) {
            var fixedCount = 0
            for (i in 0 until status.satelliteCount) {
                if (status.usedInFix(i)) {
                    fixedCount++
                }
            }
            _satelliteCount.value = fixedCount

            if (fixedCount > 0 && _connectionStatus.value != GpsConnectionStatus.CONNECTED) {
                _connectionStatus.value = GpsConnectionStatus.SEARCHING
            } else if (fixedCount == 0 && _connectionStatus.value == GpsConnectionStatus.CONNECTED) {
                _connectionStatus.value = GpsConnectionStatus.SEARCHING
            }
        }

        override fun onStarted() {
            _connectionStatus.value = GpsConnectionStatus.SEARCHING
        }

        override fun onStopped() {
            _connectionStatus.value = GpsConnectionStatus.NOT_CONNECTED
            _satelliteCount.value = 0
        }
    }

    @SuppressLint("MissingPermission")
    override fun startTracking() {
        val manager = locationManager ?: return
        if (isTracking) return

        try {
            val isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGpsEnabled) {
                _connectionStatus.value = GpsConnectionStatus.NOT_CONNECTED
                return
            }

            _connectionStatus.value = GpsConnectionStatus.SEARCHING

            manager.registerGnssStatusCallback(
                gnssStatusCallback,
                Handler(Looper.getMainLooper())
            )

            manager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTimeMs,
                minDistanceMeters,
                locationListener,
                Looper.getMainLooper()
            )

            // Emit last known location if available
            val lastKnown = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (lastKnown != null) {
                locationListener.onLocationChanged(lastKnown)
            }

            isTracking = true
        } catch (e: SecurityException) {
            _connectionStatus.value = GpsConnectionStatus.NOT_CONNECTED
            _satelliteCount.value = 0
            isTracking = false
        }
    }

    override fun stopTracking() {
        if (!isTracking) return
        val manager = locationManager
        try {
            manager?.removeUpdates(locationListener)
            manager?.unregisterGnssStatusCallback(gnssStatusCallback)
        } catch (e: Exception) {
            // Ignore unregistration errors on shutdown
        } finally {
            isTracking = false
            _connectionStatus.value = GpsConnectionStatus.NOT_CONNECTED
            _satelliteCount.value = 0
        }
    }
}


package com.autonav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.autonav.data.repository.DefaultSystemStatusRepository
import com.autonav.location.AndroidGpsLocationClient
import com.autonav.location.GpsLocationClient
import com.autonav.ui.screens.HomeScreen
import com.autonav.ui.theme.AutoNavTheme
import com.autonav.viewmodel.HomeViewModel

/**
 * Main entry point for the standalone AutoNav automotive infotainment application.
 *
 * Manages the lifecycle of hardware GNSS location clients and feeds vehicle telemetry
 * into the application state repository.
 */
class MainActivity : ComponentActivity() {

    private val systemStatusRepository by lazy {
        DefaultSystemStatusRepository()
    }

    private val gpsLocationClient: GpsLocationClient by lazy {
        AndroidGpsLocationClient(applicationContext)
    }

    private val homeViewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    statusRepository = systemStatusRepository,
                    scope = lifecycleScope
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Attach vehicle GNSS sensor client to the telemetry repository
        systemStatusRepository.attachGpsClient(gpsLocationClient, lifecycleScope)

        setContent {
            AutoNavTheme {
                HomeScreen(
                    viewModel = homeViewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        gpsLocationClient.startTracking()
    }

    override fun onStop() {
        super.onStop()
        gpsLocationClient.stopTracking()
    }
}

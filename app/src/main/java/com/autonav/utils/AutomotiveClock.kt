package com.autonav.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Provides formatted date and time strings for the automotive infotainment header.
 */
object AutomotiveClock {

    private val timeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("HH:mm", Locale.US)

    private val dateFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.US)

    fun formatTime(dateTime: LocalDateTime = LocalDateTime.now()): String {
        return dateTime.format(timeFormatter)
    }

    fun formatDate(dateTime: LocalDateTime = LocalDateTime.now()): String {
        return dateTime.format(dateFormatter)
    }

    fun formatHeaderTimestamp(dateTime: LocalDateTime = LocalDateTime.now()): String {
        return "${formatDate(dateTime)}  \u2022  ${formatTime(dateTime)}"
    }
}

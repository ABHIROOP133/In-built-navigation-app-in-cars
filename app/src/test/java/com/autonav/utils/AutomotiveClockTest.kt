package com.autonav.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class AutomotiveClockTest {

    @Test
    fun `formatTime and formatDate produce expected automotive header strings`() {
        val sampleDateTime = LocalDateTime.of(2026, 11, 11, 19, 25)

        assertEquals("19:25", AutomotiveClock.formatTime(sampleDateTime))
        assertEquals("Wed, 11 Nov 2026", AutomotiveClock.formatDate(sampleDateTime))
        assertEquals("Wed, 11 Nov 2026  \u2022  19:25", AutomotiveClock.formatHeaderTimestamp(sampleDateTime))
    }
}

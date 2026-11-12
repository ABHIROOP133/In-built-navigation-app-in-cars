package com.autonav.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class SpeedFormatterTest {

    @Test
    fun `metersPerSecondToKmh converts correctly`() {
        assertEquals(0f, SpeedFormatter.metersPerSecondToKmh(0f), 0.01f)
        assertEquals(36f, SpeedFormatter.metersPerSecondToKmh(10f), 0.01f)
        assertEquals(100f, SpeedFormatter.metersPerSecondToKmh(27.7778f), 0.1f)
        assertEquals(0f, SpeedFormatter.metersPerSecondToKmh(-5f), 0.01f)
    }

    @Test
    fun `formatSpeed produces clean glanceable automotive strings`() {
        assertEquals("45 km/h", SpeedFormatter.formatSpeed(45.4f))
        assertEquals("80 km/h", SpeedFormatter.formatSpeed(79.8f))
        assertEquals("0 km/h", SpeedFormatter.formatSpeed(0f))
        assertEquals("-- km/h", SpeedFormatter.formatSpeed(null))
        assertEquals("-- km/h", SpeedFormatter.formatSpeed(-1f))
    }

    @Test
    fun `bearingToCardinal returns correct 8-point compass cardinals`() {
        assertEquals("N", SpeedFormatter.bearingToCardinal(0f))
        assertEquals("N", SpeedFormatter.bearingToCardinal(355f))
        assertEquals("NE", SpeedFormatter.bearingToCardinal(45f))
        assertEquals("E", SpeedFormatter.bearingToCardinal(90f))
        assertEquals("SE", SpeedFormatter.bearingToCardinal(135f))
        assertEquals("S", SpeedFormatter.bearingToCardinal(180f))
        assertEquals("SW", SpeedFormatter.bearingToCardinal(225f))
        assertEquals("W", SpeedFormatter.bearingToCardinal(270f))
        assertEquals("NW", SpeedFormatter.bearingToCardinal(315f))
        assertEquals("--", SpeedFormatter.bearingToCardinal(null))
    }
}


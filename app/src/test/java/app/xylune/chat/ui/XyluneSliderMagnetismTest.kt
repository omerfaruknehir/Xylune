package app.xylune.chat.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class XyluneSliderMagnetismTest {
    private val range = 0f..6f

    @Test
    fun `values outside a detent influence radius stay unchanged`() {
        val value = 0.5f
        assertEquals(value, magneticSliderValue(value, range, anchorCount = 7), 0.0001f)
    }

    @Test
    fun `nearby values are pulled toward the nearest detent without hard snapping`() {
        val raw = 2.18f
        val magnetized = magneticSliderValue(raw, range, anchorCount = 7)

        assertTrue(abs(magnetized - 2f) < abs(raw - 2f))
        assertTrue(magnetized > 2f)
        assertTrue(magnetized < raw)
    }

    @Test
    fun `magnetic pull is symmetric around a detent`() {
        val left = magneticSliderValue(2.2f, range, anchorCount = 7)
        val right = magneticSliderValue(3.8f, range, anchorCount = 7)

        assertEquals(2f - left, right - 4f, 0.0001f)
    }

    @Test
    fun `detents and range endpoints remain exact`() {
        assertEquals(3f, magneticSliderValue(3f, range, anchorCount = 7), 0.0001f)
        assertEquals(0f, magneticSliderValue(-2f, range, anchorCount = 7), 0.0001f)
        assertEquals(6f, magneticSliderValue(8f, range, anchorCount = 7), 0.0001f)
    }
}

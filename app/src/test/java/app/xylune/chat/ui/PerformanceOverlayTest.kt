package app.xylune.chat.ui

import app.xylune.chat.settings.DeveloperSettings
import app.xylune.chat.settings.PerformanceOverlayPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PerformanceOverlayTest {
    @Test
    fun updateIntervalIsBounded() {
        assertEquals(250, normalizedPerformanceIntervalMs(1))
        assertEquals(500, normalizedPerformanceIntervalMs(500))
        assertEquals(2_000, normalizedPerformanceIntervalMs(9_000))
    }

    @Test
    fun percentileUsesNearestRank() {
        val values = (1..100).map(Int::toDouble)
        assertEquals(95.0, performancePercentile(values, 0.95), 0.0)
        assertEquals(99.0, performancePercentile(values, 0.99), 0.0)
        assertEquals(0.0, performancePercentile(emptyList(), 0.95), 0.0)
    }

    @Test
    fun missedFramesAreEstimatedFromFrameBudget() {
        assertEquals(0, estimatedMissedFrames(8.0, 16.67))
        assertEquals(0, estimatedMissedFrames(16.67, 16.67))
        assertEquals(1, estimatedMissedFrames(20.0, 16.67))
        assertEquals(2, estimatedMissedFrames(40.0, 16.67))
    }

    @Test
    fun developerSettingsNormalizeBoundsAndKeepDiagnosticsOffByDefault() {
        val defaults = DeveloperSettings()
        assertTrue(!defaults.enabled)
        assertTrue(!defaults.toolDiagnosticsEnabled)
        assertTrue(!defaults.performanceOverlayEnabled)
        assertTrue(!defaults.diagnosticProfilerEnabled)
        assertEquals(PerformanceOverlayPosition.TOP_END, defaults.performanceOverlayPosition)
        assertEquals(250, defaults.copy(performanceUpdateIntervalMs = 1).normalized().performanceUpdateIntervalMs)
        assertEquals(2_000, defaults.copy(performanceUpdateIntervalMs = 5_000).normalized().performanceUpdateIntervalMs)
        assertEquals(0f, defaults.copy(performanceOverlayBackgroundOpacity = -1f).normalized().performanceOverlayBackgroundOpacity)
        assertEquals(1f, defaults.copy(performanceOverlayBackgroundOpacity = 2f).normalized().performanceOverlayBackgroundOpacity)
        assertEquals(0f, defaults.copy(performanceOverlayTextOpacity = -1f).normalized().performanceOverlayTextOpacity)
        assertEquals(0.60f, defaults.copy(performanceOverlayScale = 0.1f).normalized().performanceOverlayScale)
        assertEquals(2.00f, defaults.copy(performanceOverlayScale = 5f).normalized().performanceOverlayScale)
    }

    @Test
    fun frameIntervalAndFpsAreReciprocals() {
        val fps = 70.0
        val intervalMs = 1_000.0 / fps
        assertEquals(fps, 1_000.0 / intervalMs, 0.0001)
    }

    @Test
    fun highRefreshBudgetFlagsOnlyActuallyMissedVsyncs() {
        val budget120Hz = 1_000.0 / 120.0
        assertEquals(0, estimatedMissedFrames(8.0, budget120Hz))
        assertEquals(1, estimatedMissedFrames(16.6, budget120Hz))
        assertEquals(3, estimatedMissedFrames(33.3, budget120Hz))
    }

    @Test
    fun causeDetectorAttributesGpuPressureWhileBlurIsActive() {
        val cause = detectLikelyBottleneck(
            PerformanceCauseInput(
                refreshRateHz = 120f,
                fps = 30.0,
                frameTotalMs = 30.0,
                frameDurationP95Ms = 33.0,
                jankPercent = 20.0,
                gpuMs = 19.0,
                inputMs = 0.1,
                animationMs = 0.2,
                layoutMs = 1.0,
                drawMs = 2.0,
                syncMs = 0.5,
                commandMs = 2.0,
                swapMs = 1.0,
                blurCpuMs = 0.5,
                blurFrames = 30,
                blurSourceDrawsPerFrame = 1.0,
                appRecompositionsPerSecond = 10.0,
                chatRecompositionsPerSecond = 10.0,
                allocationMbPerSecond = 2.0,
                blockingGcPerSecond = 0.0,
            ),
        )
        assertEquals("GPU rendering (blur active)", cause)
    }

    @Test
    fun healthyGpuStageIsNotBlamedForSchedulingSpikes() {
        val cause = detectLikelyBottleneck(
            PerformanceCauseInput(
                refreshRateHz = 120f,
                fps = 98.0,
                frameTotalMs = 11.0,
                frameDurationP95Ms = 25.0,
                jankPercent = 4.0,
                gpuMs = 2.5,
                inputMs = 0.1,
                animationMs = 0.1,
                layoutMs = 0.1,
                drawMs = 2.2,
                syncMs = 0.4,
                commandMs = 1.4,
                swapMs = 0.7,
                blurCpuMs = 0.12,
                blurFrames = 49,
                blurSourceDrawsPerFrame = 1.0,
                appRecompositionsPerSecond = 0.0,
                chatRecompositionsPerSecond = 0.0,
                allocationMbPerSecond = 3.0,
                blockingGcPerSecond = 0.0,
            ),
        )
        assertEquals("Frame pacing / scheduling stalls", cause)
    }

    @Test
    fun duplicateBlurTraversalIsReportedBeforeGenericGpuAttribution() {
        val cause = detectLikelyBottleneck(
            PerformanceCauseInput(
                refreshRateHz = 120f,
                fps = 80.0,
                frameTotalMs = 12.0,
                frameDurationP95Ms = 20.0,
                jankPercent = 8.0,
                gpuMs = 3.0,
                inputMs = 0.1,
                animationMs = 0.1,
                layoutMs = 0.3,
                drawMs = 2.0,
                syncMs = 0.4,
                commandMs = 1.0,
                swapMs = 0.5,
                blurCpuMs = 0.5,
                blurFrames = 40,
                blurSourceDrawsPerFrame = 2.0,
                appRecompositionsPerSecond = 0.0,
                chatRecompositionsPerSecond = 0.0,
                allocationMbPerSecond = 2.0,
                blockingGcPerSecond = 0.0,
            ),
        )
        assertEquals("Duplicate content recording for blur", cause)
    }

    @Test
    fun causeDetectorPrioritizesBlockingGcPressure() {
        val cause = detectLikelyBottleneck(
            PerformanceCauseInput(
                refreshRateHz = 120f,
                fps = 40.0,
                frameTotalMs = 24.0,
                frameDurationP95Ms = 30.0,
                jankPercent = 20.0,
                gpuMs = 4.0,
                inputMs = 0.2,
                animationMs = 0.2,
                layoutMs = 2.0,
                drawMs = 3.0,
                syncMs = 0.3,
                commandMs = 1.0,
                swapMs = 0.5,
                blurCpuMs = 0.2,
                blurFrames = 20,
                blurSourceDrawsPerFrame = 1.0,
                appRecompositionsPerSecond = 10.0,
                chatRecompositionsPerSecond = 10.0,
                allocationMbPerSecond = 40.0,
                blockingGcPerSecond = 1.0,
            ),
        )
        assertEquals("Allocation / blocking GC pressure", cause)
    }

    @Test
    fun renderedFrameEstimatorCannotExceedThePhysicalDisplayRate() {
        assertEquals(120.0, boundedRenderedFrameRate(130.0, 120f), 0.0)
        assertEquals(93.5, boundedRenderedFrameRate(93.5, 120f), 0.0)
        assertEquals(0.0, boundedRenderedFrameRate(-4.0, 120f), 0.0)
    }

    @Test
    fun causeProfileIncludesConfidenceSeverityAndEvidence() {
        val profile = analyzePerformanceCause(
            PerformanceCauseInput(
                refreshRateHz = 120f,
                fps = 55.0,
                frameTotalMs = 17.0,
                frameDurationP95Ms = 20.0,
                jankPercent = 10.0,
                gpuMs = 2.0,
                inputMs = 0.2,
                animationMs = 0.2,
                layoutMs = 9.0,
                drawMs = 2.0,
                syncMs = 0.3,
                commandMs = 1.0,
                swapMs = 0.5,
                blurCpuMs = 0.2,
                blurFrames = 20,
                blurSourceDrawsPerFrame = 1.0,
                appRecompositionsPerSecond = 10.0,
                chatRecompositionsPerSecond = 10.0,
                allocationMbPerSecond = 2.0,
                blockingGcPerSecond = 0.0,
            ),
        )
        assertEquals("Layout / measure", profile.primaryCause)
        assertTrue(profile.confidencePercent in 40..99)
        assertNotEquals(PerformanceSeverity.HEALTHY, profile.severity)
        assertTrue(profile.evidence.contains("frame budget"))
    }
}

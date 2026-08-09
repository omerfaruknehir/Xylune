package app.xylune.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ArchitectureTimingRegressionTest {
    private fun source(name: String): String = File("src/main/java/app/xylune/chat/$name").readText()

    @Test
    fun `ui navigation and popup lifecycles do not depend on arbitrary sleeps`() {
        assertFalse(source("ui/XyluneApp.kt").contains("delay(8_000"))
        assertFalse(source("ui/LinkPreview.kt").contains("delay(170"))
        assertFalse(source("LauncherActivity.kt").contains("postDelayed"))
        assertTrue(source("ui/LinkPreview.kt").contains("visibility.isIdle"))
        assertTrue(source("LauncherActivity.kt").contains("doOnPreDraw"))
    }

    @Test
    fun `settings has one navigation owner`() {
        assertFalse(File("src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt").exists())
        val settings = source("ui/SettingsScreen.kt")
        assertTrue(settings.contains("SettingsRoute.LANGUAGE"))
        assertFalse(settings.contains("delay(300)"))
        assertFalse(settings.contains("delay(90)"))
    }
}

package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemePaletteRegressionTest {
    @Test
    fun `Turp is radish themed and Arbor preserves the original green palette`() {
        val theme = File("src/main/java/app/turp/chat/ui/theme/Theme.kt").readText()
        val preferences = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()

        assertTrue(preferences.contains("TURP, ARBOR, SYSTEM"))
        assertTrue(theme.contains("private val TurpLight"))
        assertTrue(theme.contains("primary = Color(0xFFA51D45)"))
        assertTrue(theme.contains("primary = Color(0xFFFFB1C5)"))
        assertTrue(theme.contains("private val ArborLight"))
        assertTrue(theme.contains("primary = Color(0xFF286448)"))
        assertTrue(theme.contains("private val ArborDark"))
        assertTrue(theme.contains("primary = Color(0xFF99D5B1)"))
        assertTrue(settings.contains("ColorPalette.ARBOR -> "Arbor""))
    }
}
